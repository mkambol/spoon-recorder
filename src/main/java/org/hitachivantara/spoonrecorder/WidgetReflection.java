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

import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Widget;
import org.jooq.lambda.Seq;
import org.pentaho.di.ui.core.gui.GUIResource;
import org.pentaho.di.ui.core.widget.TableView;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.google.common.base.Strings.isNullOrEmpty;
import static org.jooq.lambda.Seq.seq;

class WidgetReflection {


  static String getText( Widget w ) {
    if ( w instanceof TableView ) {
      return getTableText( (TableView) w );
    }
    Object result = getter( w, "getText" );
    final String text = result == null ? null : result.toString();
    return isNullOrEmpty( text ) ? null : text.replace( "\n", "#NL#" );
  }

  static Table getParentTable( Widget w ) {
    Control c = (Control) w;
    boolean foundTable = false;
    while ( c.getParent() != null && !foundTable ) {
      foundTable = c.getParent() instanceof Table;
      c = c.getParent();
    }
    return  foundTable ? (Table) c : null;
  }

  static String getImage( Widget w ) {
    Object result = getter( w, "getImage" );
    if ( result != null ) {
      Optional<String> imageLoc = seq( GUIResource.getInstance().getImageMap().entrySet() )
        .filter( t -> t.getValue().equals( result ) )
        .findSingle()
        .map( Map.Entry::getKey );
      if ( imageLoc.isPresent() ) {
        return imageLoc.get();
      }
    }
    return null;
  }

  private static String getTableText( TableView tableView ) {

    Table table = tableView.getTable();
    return Seq.of( table.getItems() )
      .crossJoin( Seq.range( 1, table.getColumnCount() ) )
      .map( t -> {
        if ( t.v2.equals( 0 ) ) {
          return "row=" + t.v1.getText( t.v2 );
        } else {
          return t.v1.getText( t.v2 );
        }

      } )
      .collect( Collectors.joining( "," ) );
  }


  private static Object getter( Widget w, String methodName ) {
    Optional<Method> m;
    try {
      m = findMethod( w, methodName );
      if ( m.isPresent() ) {
        return m.get().invoke( w );

      }
    } catch ( IllegalAccessException | InvocationTargetException e ) {
      e.printStackTrace();
    }
    return null;
  }

  private static Optional<Method> findMethod( Widget w, String methodName ) {
    return Arrays.stream( w.getClass().getDeclaredMethods() )
      .filter( meth -> meth.getName().equals( methodName ) )
      .filter( meth -> meth.getParameterCount() == 0 )
      .findFirst();
  }


}
