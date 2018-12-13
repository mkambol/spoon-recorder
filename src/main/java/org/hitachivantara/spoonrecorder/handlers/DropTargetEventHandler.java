package org.hitachivantara.spoonrecorder.handlers;

import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.widgets.Widget;
import org.hitachivantara.spoonrecorder.SWTRecordedEvent;
import org.hitachivantara.spoonrecorder.WidgetKey;

import java.util.function.Consumer;

public class DropTargetEventHandler implements RecordEventHandler {
  @Override public boolean attachListener( WidgetKey key, Widget w, int eventType,
                                           Consumer<SWTRecordedEvent> recordedEventConsumer ) {
    if ( w instanceof DropTarget && eventType == SWT.Selection ) {
      DropTarget dropTarget = (DropTarget) w;

      dropTarget.addDropListener( new DropTargetAdapter() {
        public void drop( DropTargetEvent event ) {
          System.out.println( "foo" );
        }
      } );
      return true;
    }
    return false;
  }
}
