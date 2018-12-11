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

import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.TableItem;
import org.pentaho.di.ui.core.widget.TableView;

public class SWTRecordedEvent {


  protected final WidgetKey key;
  final Event event;

  private static BiMap<Integer, String> types =
    ImmutableBiMap.of(
      SWT.Modify, "modify",
      SWT.Selection, "selection",
      SWT.MouseDoubleClick, "verify",
      SWT.MouseDown, "mouseDown" );


  private SWTRecordedEvent( WidgetKey key, Event e ) {
    this.key = key;
    this.event = e;
  }

  static SWTRecordedEvent to( WidgetKey key, Event e ) {
    //    if ( e.widget instanceof Text || e.widget instanceof CCombo ) {
    //      return new SWTTextEvent( key, e );
    //    } else
    if ( e.widget instanceof TableView ) {
      return new SWTTableViewEvent( key, e );
    } else if ( e.widget instanceof CTabFolder ) {
      return new SWTCTabFolderEvent( key, e );
    } else {
      return new SWTRecordedEvent( key, e );
    }
  }

  static int swtEvent( String type ) {
    return types.inverse().get( type );
  }

  protected String getText() {
    switch ( event.type ) {
      case SWT.Modify:
      case SWT.MouseDoubleClick:
        final String text = WidgetReflection.getText( event.widget );
        System.out.println( text );
        return text;
      case SWT.MouseDown:
      case SWT.Selection:
      default:
        return "";
    }
  }

  @Override public String toString() {
    return key.keyStr() + "\t" + types.get( event.type )
      + "\t" + event.widget.getClass().getSimpleName() + "\t"
      + getText() + "\n";
  }

  public WidgetKey getKey() {
    return key;
  }

  boolean provisional() {
    return event.type != SWT.MouseDoubleClick
      && !( event.widget instanceof TableView );
  }


  private static class SWTTableViewEvent extends SWTRecordedEvent {
    private SWTTableViewEvent( WidgetKey key, Event e ) {
      super( key, e );
    }

    @Override protected String getText() {
      TableView tv = (TableView) event.widget;
      if ( event.type == SWT.MouseDoubleClick ) {
        return super.getText();
      } else if ( event.type == SWT.Modify ) {

        TableItem row = tv.getActiveTableItem();
        int col = tv.getActiveTableColumn();
        int rownr = row == null ? 0 : tv.getTable().indexOf( row );
        return String.format( "table[%s, %s]=%s", col, rownr, event.data );
      }
      return "";
    }

  }

  private static class SWTCTabFolderEvent extends SWTRecordedEvent {
    private SWTCTabFolderEvent( WidgetKey key, Event e ) {
      super( key, e );
    }

    @Override protected String getText() {
      CTabFolder folder = (CTabFolder) event.widget;
      return folder.getSelection().getText();
    }

  }
}




