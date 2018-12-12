/*!
 * HITACHI VANTARA PROPRIETARY AND CONFIDENTIAL
 *
 * Copyright 2018 Hitachi Vantara. All rights reserved.
 *
 * NOTICE: All information including source code contained herein is, and
 * remains the sole property of Hitachi Vantara and its licensors. The intellectual
 * and technical concepts contained herein are proprietary and confidential
 * to, and are trade secrets of Hitachi Vantara and may be covered by U.S. and foreign
 * patents, or patents in process, and are protected by trade secret and
 * copyright laws. The receipt or possession of this source code and/or related
 * information does not convey or imply any rights to reproduce, disclose or
 * distribute its contents, or to manufacture, use, or sell anything that it
 * may describe, in whole or in part. Any reproduction, modification, distribution,
 * or public display of this information without the express written authorization
 * from Hitachi Vantara is strictly prohibited and in violation of applicable laws and
 * international treaties. Access to the source code contained herein is strictly
 * prohibited to anyone except those individuals and entities who have executed
 * confidentiality and non-disclosure agreements or other agreements with Hitachi Vantara,
 * explicitly covering such access.
 */

package org.hitachivantara.spoonrecorder;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.Widget;
import org.jooq.lambda.tuple.Tuple2;
import org.jooq.lambda.tuple.Tuple3;
import org.pentaho.di.ui.core.widget.TableView;

import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.EventListener;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Arrays.asList;
import static org.hitachivantara.spoonrecorder.WidgetReflection.getParentTable;
import static org.jooq.lambda.tuple.Tuple.tuple;

public class SWTRecorder implements Closeable {

  private final Shell shell;
  private final Path outPath;
  private BufferedWriter writer;
  private Tuple2<WidgetKey, String> lastWrittenEvent;
  private SWTTreeWatcher watcher;
  private Set<Tuple3<Integer, Widget, Listener>> listeners = new HashSet<>();
  private Set<EventListener> eventListeners = new HashSet<>();
  private AtomicBoolean closed = new AtomicBoolean( false );

  public SWTRecorder( Shell parent, Path outPath ) {
    this.outPath = outPath;
    this.shell = parent;
  }

  public void open() {
    try {
      Files.deleteIfExists( outPath );
      writer = Files.newBufferedWriter( outPath, UTF_8 );
      watcher = new SWTTreeWatcher(
        shell.getDisplay(), this::attachListeners );
    } catch ( IOException e ) {
      e.printStackTrace();
    }
  }

  @Override public void close() throws IOException {
    closed.set( true );
    listeners
      .forEach( t -> {
        if ( !t.v2.isDisposed() ) {
          t.v2.removeListener( t.v1, t.v3 );
        }
      } ); // tuple = ( type, control, listener )
    watcher.close();
    if ( lastWrittenEvent != null ) {
      writer.write( lastWrittenEvent.v2 );
    }
    writer.close();
  }


  private void attachListeners( Tuple2<Boolean, WidgetKey> key, Widget c ) {
    if ( skipWidgetType( c ) ) {
      return;
    }
    if ( key.v1 ) {
      asList( SWT.MouseDown, SWT.MouseDoubleClick, SWT.Modify )
        .forEach( listenerForType( key, c ) );
    }
  }

  private Consumer<Integer> listenerForType( Tuple2<Boolean, WidgetKey> key, Widget c ) {
    return type -> {
      if ( c instanceof Menu ) {
        addMenuListener( c, key.v2 );
      } else if ( c instanceof TableView ) {
        addTableViewListener( c, key.v2 );
      } else if ( getParentTable( c ) == null ) {
        final Listener listener = e -> writeEvent( SWTRecordedEvent.to( key.v2, e ) );
        listeners.add( tuple( type, c, listener ) );
        c.addListener( type, listener );
      }
    };

  }

  private void addMenuListener( Widget w, WidgetKey key ) {
  }


  private boolean skipWidgetType( Widget c ) {
    return c instanceof Table  // tables are embedded in TableView, no need to capture them twice
      || c instanceof ToolBar  // excluding toolbar and canvas for the time being to avoid
      || c instanceof Canvas  // some noise.
      || ( c instanceof Combo && ( "100%".equals( ( (Combo) c ).getText() ) ) );
  }


  private void addTableViewListener( Widget c, WidgetKey key ) {
    if ( c instanceof TableView ) {
      TableView tv = ( (TableView) c );
      final MouseAdapter mouseListener = new MouseAdapter() {
        @Override public void mouseDoubleClick( MouseEvent mouseEvent ) {
          super.mouseDoubleClick( mouseEvent );
          writeEvent( SWTRecordedEvent.to( key, event( tv, SWT.MouseDoubleClick, mouseEvent.data ) ) );
        }
      };
      eventListeners.add( mouseListener );
      tv.getTable().addMouseListener( mouseListener );
      ModifyListener originalModifyListener = tv.getContentListener();
      final ModifyListener modifyListener = modifyEvent -> {
        if ( originalModifyListener != null ) {
          originalModifyListener.modifyText( modifyEvent );
        }
        writeEvent( SWTRecordedEvent.to( key, event( tv, SWT.Modify, modifyEvent.data ) ) );
      };
      eventListeners.add( modifyListener );

      tv.setContentListener( modifyListener );
    }
  }

  private Event event( Widget w, int type, Object data ) {
    Event e = new Event();
    e.widget = w;
    e.type = type;
    e.data = data;
    return e;
  }

  private void writeEvent( SWTRecordedEvent recordedEvent ) {
    if ( closed.get() ) {
      return;
    }
    try {
      final String event = recordedEvent.toString();
      System.out.println( event );
      if ( recordedEvent.provisional() ) {
        if ( lastWrittenEvent != null && !recordedEvent.getKey().equals( lastWrittenEvent.v1 ) ) {
          // if the same event key has two back to back events, only write the last one.
          // that avoids noisy event sequences where each key press gets recorded.
          writer.write( lastWrittenEvent.v2 );
        }
        lastWrittenEvent = tuple( recordedEvent.getKey(), recordedEvent.toString() );
      } else {
        if ( lastWrittenEvent != null ) {
          writer.write( lastWrittenEvent.v2 );
          lastWrittenEvent = null;
        }
        writer.write( event );
      }
      writer.flush();
    } catch ( IOException e ) {
      throw new IllegalStateException( e );
    }
  }


}
