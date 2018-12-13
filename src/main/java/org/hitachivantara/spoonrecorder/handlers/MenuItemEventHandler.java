package org.hitachivantara.spoonrecorder.handlers;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Widget;
import org.hitachivantara.spoonrecorder.SWTRecordedEvent;
import org.hitachivantara.spoonrecorder.WidgetKey;
import org.jooq.lambda.Seq;

import java.util.function.Consumer;

public class MenuItemEventHandler implements RecordEventHandler {
  @Override public boolean attachListener( WidgetKey key, Widget w, int eventType,
                                           Consumer<SWTRecordedEvent> recordedEventConsumer ) {
    if ( w instanceof MenuItem && eventType == SWT.Selection ) {
      MenuItem mi = (MenuItem) w;

      Listener[] listeners = mi.getListeners( SWT.Selection );
      Seq.of( listeners )
        .forEach( listener -> mi.removeListener( SWT.Selection, listener ) );
      mi.addSelectionListener( new SelectionAdapter() {
        @Override public void widgetSelected( SelectionEvent selectionEvent ) {
          recordedEventConsumer.accept( SWTRecordedEvent.to( key, event( w, SWT.Selection, null ) ) );
          super.widgetSelected( selectionEvent );
        }
      } );
      Seq.of( listeners )
        .forEach( listener -> mi.addListener( SWT.Selection, listener ) );
      return true;
    }
    return false;
  }
}
