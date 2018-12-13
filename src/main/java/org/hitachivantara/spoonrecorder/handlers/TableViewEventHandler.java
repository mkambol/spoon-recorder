package org.hitachivantara.spoonrecorder.handlers;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.widgets.Widget;
import org.hitachivantara.spoonrecorder.SWTRecordedEvent;
import org.hitachivantara.spoonrecorder.WidgetKey;
import org.pentaho.di.ui.core.widget.TableView;

import java.util.function.Consumer;

public class TableViewEventHandler implements RecordEventHandler {

  @Override public boolean attachListener( WidgetKey key, Widget w, int eventType,
                                           Consumer<SWTRecordedEvent> recordedEventConsumer ) {
    if ( w instanceof TableView ) {
      TableView tv = ( (TableView) w );
      final MouseAdapter mouseListener = new MouseAdapter() {
        @Override public void mouseDoubleClick( MouseEvent mouseEvent ) {
          super.mouseDoubleClick( mouseEvent );
          recordedEventConsumer
            .accept( SWTRecordedEvent.to( key, event( tv, SWT.MouseDoubleClick, mouseEvent.data ) ) );
        }
      };
      tv.getTable().addMouseListener( mouseListener );
      ModifyListener originalModifyListener = tv.getContentListener();
      final ModifyListener modifyListener = modifyEvent -> {
        if ( originalModifyListener != null ) {
          originalModifyListener.modifyText( modifyEvent );
        }
        recordedEventConsumer.accept( SWTRecordedEvent.to( key, event( tv, SWT.Modify, modifyEvent.data ) ) );
      };
      tv.setContentListener( modifyListener );
      return true;
    }
    return false;
  }
}
