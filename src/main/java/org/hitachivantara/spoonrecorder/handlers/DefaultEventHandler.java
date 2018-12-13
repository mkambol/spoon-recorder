package org.hitachivantara.spoonrecorder.handlers;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Widget;
import org.hitachivantara.spoonrecorder.SWTRecordedEvent;
import org.hitachivantara.spoonrecorder.WidgetKey;

import java.util.function.Consumer;


public class DefaultEventHandler implements RecordEventHandler {


  @Override public boolean attachListener( WidgetKey key, Widget w, int eventType,
                                           Consumer<SWTRecordedEvent> recordedEventConsumer ) {
    if ( eventType != SWT.Selection ) {
      Listener listener = e -> recordedEventConsumer.accept( SWTRecordedEvent.to( key, e ) );
      w.addListener( eventType, listener );
    }
    return false;
  }
}
