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

import com.google.common.base.Splitter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.widgets.*;
import org.jooq.lambda.tuple.Tuple2;
import org.pentaho.di.ui.core.widget.TableView;
import org.pentaho.di.ui.spoon.Spoon;

import java.io.Closeable;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;

import static org.hitachivantara.spoonrecorder.SWTRecordedEvent.swtEvent;
import static org.jooq.lambda.Seq.seq;

public class SWTPlayback implements Closeable {

  private final Path eventFile;
  private final SWTTreeWatcher watcher;
  private final Map<String, CompletableFuture<Widget>> widgets = new ConcurrentHashMap<>();
  private final Display display;
  private static final int MAX_WAIT = 2000;
  private Executor executor = Executors.newSingleThreadExecutor();
  private AtomicReference<String> error = new AtomicReference<>();
  private static final int MAX_RETRIES = 5;

  private Semaphore semaphore = new Semaphore( 1 );


  public SWTPlayback( Display display, Path eventFile ) {
    this.eventFile = eventFile;
    this.display = display;
    watcher = new SWTTreeWatcher( display, this::treeAction );
  }

  private void treeAction( Tuple2<Boolean, WidgetKey> k, Widget c ) {
    if ( k != null && k.v2 != null ) {
      final String key = k.v2.keyStr();
      if ( !widgets.containsKey( key ) ) {
        widgets.put( key, new CompletableFuture<>() );
      }
      widgets.get( key ).complete( c );
    }
  }

  public void open() {
    try {
      List<String> events = Files.readAllLines( eventFile );
      executor.execute( () -> {
          seq( events ).forEach( this::replayEvent );
          if ( error.get() != null ) {
            close();
          }
        }
      );
    } catch ( IOException e ) {
      throw new IllegalStateException( e );
    }
  }

  public Optional<String> getError() {
    return Optional.ofNullable( error.get() );
  }


  private void replayEvent( String line ) {
    if ( error.get() != null ) {
      return;
    }
    System.out.println( "line:;  " + line );
    sleep( 700 );



    List<String> split = Splitter.on( "\t" ).splitToList( line );
    System.out.println( split );
    String key = split.get( 0 );
    String eventName = split.get( 1 );
    String val = split.get( 3 );
    Event event = EventSerializer.deserialize( split.get( 4 ) );

    widgets.putIfAbsent( key, new CompletableFuture<>() );
    Widget c;
    try {
      c = getWidget( key );

      switch ( swtEvent( eventName ) ) {
        case SWT.EraseItem:
          display.asyncExec( () -> eraseItem( c, val, event ) );
          break;
        case SWT.Close:
          display.syncExec( () -> closeEvent( c, val, event ) );
          break;
        case SWT.Modify:
          display.syncExec( () -> setText( c, val ) );
          break;
        case SWT.Hide:
          display.asyncExec( () -> hideControl( c, val ) );
          // fall through
        default:
          try {
        //    semaphore.acquire();
            display.asyncExec( () -> generalEvent( c, val, event ) );
            break;
          } catch ( Exception e ) {
            e.printStackTrace();
          }

      }
    } catch ( InterruptedException | TimeoutException | ExecutionException e ) {
      // Couldn't get control before timeout.  :(
      error.set( "Failed on line " + line + "\n" + e.getMessage() );
    }
  }

  // hacky
  private void eraseItem( Widget c, String val, Event event ) {
    if ( c instanceof TableView && val != null ) {
      try {
        val = val.replace( "[", "" ).replace( "]", "" );
        int index = Integer.valueOf( val );
        ( (TableView) c ).remove( index );
      } catch ( Exception e ) {
        e.printStackTrace();
      }

    }
  }

  private void closeEvent( Widget c, String val, Event event ) {
    if ( c == Spoon.getInstance().getShell() ) {
      System.exit( 0 );
      // display.close();
    } else {
      generalEvent( c, val, event );
    }
  }

  private void hideControl( Widget c, String val ) {
    if ( c instanceof Menu ) {
      ( (Menu) c ).setVisible( false );
    }
  }

  private Widget getWidget( String key )
    throws InterruptedException, ExecutionException, TimeoutException {
    Widget w;
    w = widgets.get( key ).get( MAX_WAIT, TimeUnit.MILLISECONDS );
    if ( w.isDisposed() ) {
      // if displosed, clear it out and see if a new one loads.
      // Possible a new instance of the same control has been created
      widgets.put( key, new CompletableFuture<>() );
      w = widgets.get( key ).get( MAX_WAIT, TimeUnit.MILLISECONDS );
    }
    return w;
  }

  private void verify( String line, Widget c, String expectedText ) {
    if ( !verifyWithRetries( c, expectedText, MAX_RETRIES ) ) {
      error.set( "Failed on line " + line + "\n"
        + "expected:  " + expectedText + "\nfound:     " + WidgetReflection.getText( c ) );
    }
  }

  private boolean verifyWithRetries( Widget c, String expectedText, int retries ) {
    final String actual = WidgetReflection.getText( c );
    if ( expectedText != null && newLineSub( expectedText ).equals( actual ) ) {
      return true;
    }
    if ( retries > 0 ) {
      sleep( 200 );
      return verifyWithRetries( c, expectedText, retries - 1 );
    }
    return false;
  }

  private String newLineSub( String t ) {
    return t.replace( "#NL#", "\n" );
  }

  private void setText( Widget widget, String text ) {
    if ( widget instanceof TableView ) {
      setTableText( widget, text );
      return;
    }
    try {
      Method method = widget.getClass().getMethod( "setText", String.class );
      method.invoke( widget, newLineSub( text ) );
    } catch ( NoSuchMethodException | IllegalAccessException | InvocationTargetException e ) {
      error.set( "Failed to set text on control " + widget.getClass() );
    }
  }

  private void setTableText( Widget widget, String text ) {
    if ( text == null || !text.startsWith( "table[" ) ) {
      return;
    }
    // value is a cell in the table, identified by coords 'table[colnr,rownr]=value'
    List<String> coordsAndValue = Splitter.on( "=" ).splitToList( text );
    final String tableXY = coordsAndValue.get( 0 );
    List<String> coords = Splitter.on( "," ).splitToList(
      tableXY.substring( 6, tableXY.length() - 1 ) );
    int colnr = Integer.parseInt( coords.get( 0 ).trim() );
    int rownr = Integer.parseInt( coords.get( 1 ).trim() );

    TableView tab = (TableView) widget;

    while ( rownr >= tab.getItemCount() ) {
      tab.add( new String[ tab.getColumns().length ] );
    }

    tab.setText( coordsAndValue.get( 1 ), colnr, rownr );
  }


  private void generalEvent( Widget w, String val, Event event ) {
    try {
      if ( event.type == SWT.MouseDoubleClick && widgetWithTextVerification( w ) ) {
        verify( "", w, val );
        semaphore.release();
        return;
      }
      if ( w instanceof CTabFolder ) {
        final CTabFolder folder = (CTabFolder) w;
        for ( CTabItem item : folder.getItems() ) {
          if ( val.equals( item.getText() ) ) {
            folder.setSelection( item );
            return;
          }
        }
      }
      event.widget = w;

      if ( w instanceof Button ) {
        if ( ( w.getStyle() & SWT.CHECK ) != 0 ) {
          System.out.println( "CHECKBOXX!!!!" );
          boolean selection = ( (Button) w ).getSelection();
          ( (Button) w ).setSelection( !selection );

        }

        try {
          Method m = Widget.class.getDeclaredMethod( "sendSelection" );
          m.setAccessible( true );
          m.invoke( w );
        } catch ( IllegalAccessException | InvocationTargetException | NoSuchMethodException e ) {
          e.printStackTrace();
        }
      } else {
        try {
          Method m = Widget.class.getDeclaredMethod( "sendEvent", Event.class );
          m.setAccessible( true );
          m.invoke( w, event );
        } catch ( NoSuchMethodException | InvocationTargetException | IllegalAccessException e ) {
          e.printStackTrace();
        }
      }
    } catch (Exception e ) {
      System.out.println("FOO");
      e.printStackTrace();
    } finally {
      semaphore.release();
    }
  }

  private boolean widgetWithTextVerification( Widget w ) {
    return false;
  }


  @SuppressWarnings ( "all" )
  private void sleep( int i ) {
    try {
      Thread.sleep( i );
    } catch ( InterruptedException e ) {
      throw new AssertionError( "InterruptedException while sleeping between commands.", e );
    }
  }

  @Override public void close() {
    watcher.close();
  }
}
