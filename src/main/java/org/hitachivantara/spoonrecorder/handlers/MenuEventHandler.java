package org.hitachivantara.spoonrecorder.handlers;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MenuAdapter;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Widget;
import org.hitachivantara.spoonrecorder.SWTRecordedEvent;
import org.hitachivantara.spoonrecorder.WidgetKey;

import java.util.function.Consumer;

public class MenuEventHandler implements RecordEventHandler {
  @Override public boolean attachListener( WidgetKey key, Widget w, int eventType,
                                           Consumer<SWTRecordedEvent> recordedEventConsumer ) {
    if ( w instanceof Menu && eventType == SWT.Hide ) {

      Menu menu = (Menu) w;

      menu.addMenuListener( new MenuAdapter() {
        @Override public void menuHidden( MenuEvent menuEvent ) {
          super.menuHidden( menuEvent );
          recordedEventConsumer.accept( SWTRecordedEvent.to( key, event( w, SWT.Hide, null ) ) );
        }

        @Override public void menuShown( MenuEvent menuEvent ) {
          super.menuShown( menuEvent );
          recordedEventConsumer.accept( SWTRecordedEvent.to( key, event( w, SWT.Show, null ) ) );
        }
      } );

      return true;
    }
    return false;
  }
}
