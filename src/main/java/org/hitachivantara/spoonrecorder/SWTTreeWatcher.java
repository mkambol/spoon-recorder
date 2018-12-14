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

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Widget;
import org.jooq.lambda.Seq;
import org.jooq.lambda.tuple.Tuple2;

import java.io.Closeable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Timer;
import java.util.TimerTask;

import static com.google.common.base.Objects.firstNonNull;
import static org.hitachivantara.spoonrecorder.WidgetReflection.getMenus;
import static org.jooq.lambda.Seq.seq;

public class SWTTreeWatcher implements Closeable {

  private final Timer timer;
  private final WidgetKeyCache keyCache;
  private final Action action;

  SWTTreeWatcher( Display display, Action runnable ) {
    keyCache = new WidgetKeyCache();
    timer = new Timer();
    action = runnable;
    timer.schedule( new TimerTask() {
      @Override public void run() {
        display.asyncExec( () ->
          Seq.of( display.getShells() )
            .forEach( s -> walkShell( s, WidgetKeyCache.root( s ) ) ) );
        display.asyncExec( () -> walkMenus( display ) );
      }
    }, 0, 10 );
  }


  private void walkMenus( Display display ) {
    seq( getMenus( display ) )
      .flatMap( m -> Seq.of( m.getItems() ) )
      .forEach( menuItem -> {
        Tuple2<Boolean, WidgetKey> key = keyCache.get( menuItem, null );
        action.run( key, menuItem );
      } );
    seq( getMenus( display ) )
      .forEach( menu -> {
          Tuple2<Boolean, WidgetKey> key = keyCache.get( menu, null );
          action.run( key, menu );
        }
      );
  }

  private void walkShell( Composite composite, WidgetKey parentKey ) {
    if ( composite.getChildren().length == 0 ) {
      return;
    }
    Tuple2<Boolean, WidgetKey> compositeKey = keyCache.get( composite, parentKey );
    action.run( compositeKey, composite );
    for ( Control c : composite.getChildren() ) {
      Tuple2<Boolean, WidgetKey> key = keyCache.get( c, parentKey );
      action.run( key, c );

      if ( c instanceof Composite ) {
        walkShell( (Composite) c, firstNonNull( key.v2, compositeKey.v2 ) );
      }
    }
  }

  @Override public void close() {
    timer.cancel();
  }

  @FunctionalInterface
  interface Action {
    void run( Tuple2<Boolean, WidgetKey> key, Widget control );
  }
}
