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

import com.google.common.base.Objects;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Maps;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Widget;
import org.jooq.lambda.Seq;
import org.jooq.lambda.tuple.Tuple2;
import org.pentaho.di.ui.core.widget.ColumnInfo;
import org.pentaho.di.ui.core.widget.TableView;

import java.util.stream.Collectors;

import static org.jooq.lambda.Seq.seq;
import static org.jooq.lambda.tuple.Tuple.tuple;

public class WidgetKeyCache {
  private final BiMap<Widget, WidgetKey> widgetMap = Maps.synchronizedBiMap( HashBiMap.create() );

  public Tuple2<Boolean, WidgetKey> get( Widget w, WidgetKey parent ) {
    // todo cleanup
    String text = getText( w );
    boolean present = widgetMap.containsKey( w );

    WidgetKey wk = new InternalWidgetKey( parent, text, w.getClass(), countSameName( parent, text ) );
    if ( !present && widgetMap.containsValue( wk ) ) {
      // key was associated with different control.
      // Can happen in cases where a control is destroyed, then later recreated
      //  (as with modal dialogs that are closed and reopened)
      widgetMap.inverse().remove( wk );
    }

    widgetMap.putIfAbsent( w, wk );

    return tuple( !present, wk );
  }

  static WidgetKey root( Shell s ) {
    return new InternalWidgetKey( null, s.getText(), Shell.class, 0 );
  }

  private long countSameName( WidgetKey parent, String text ) {
    return seq( widgetMap.values() )
      .filter( key -> key.getParent() == parent )
      .filter( key -> key.getText().equals( text ) )
      .count();
  }

  private static String tabFolderText( Widget w ) {
    if ( w instanceof CTabFolder ) {
      CTabFolder folder = (CTabFolder) w;
      return Seq.of( folder.getItems() )
        .map( CTabItem::getText )
        .collect( Collectors.joining() );
    }
    return null;
  }

  private static String tableViewText( Widget w ) {
    if ( w instanceof TableView ) {
      TableView tv = (TableView) w;
      return Seq.of( tv.getColumns() )
        .map( ColumnInfo::getName )
        .collect( Collectors.joining( "," ) );
    }
    return null;
  }

  private static String getText( Widget w ) {
    return coalesce(
      WidgetReflection.getImage( w ),
      WidgetReflection.getText( w ),
      tableViewText( w ),
      tabFolderText( w ),
      "" );
  }

  private static String coalesce( String... strings ) {
    for ( String s : strings ) {
      if ( s != null ) {
        return s;
      }
    }
    return null;
  }


  private static class InternalWidgetKey implements WidgetKey {

    private WidgetKey parentKey;
    private String text;
    private Class<?> clazz;
    private long index;


    private InternalWidgetKey( WidgetKey parentKey, String text, Class<?> clazz, long index ) {
      this.parentKey = parentKey;
      this.text = text;
      this.clazz = clazz;
      this.index = index;
    }

    @Override public String getText() {
      return text;
    }

    @Override public WidgetKey getParent() {
      return parentKey;
    }

    @Override public String keyStr() {
      return keyStr( new StringBuilder() ).toString();
    }

    @Override public StringBuilder keyStr( StringBuilder b ) {
      if ( parentKey != null ) {
        parentKey.keyStr( b );
      } else {
        b.append( "ROOT" );
      }
      b.append( ".(" )
        .append( clazz.getSimpleName() )
        .append( ")" )
        .append( text )
        .append( "[" )
        .append( index )
        .append( "]" );
      return b;
    }


    @Override public boolean equals( Object o ) {
      if ( this == o ) {
        return true;
      }
      if ( o == null || getClass() != o.getClass() ) {
        return false;
      }
      InternalWidgetKey that = (InternalWidgetKey) o;
      return index == that.index
        && Objects.equal( parentKey, that.parentKey )
        && Objects.equal( text, that.text )
        && Objects.equal( clazz, that.clazz );
    }

    @Override public int hashCode() {
      return Objects.hashCode( parentKey, text, clazz, index );
    }
  }

}
