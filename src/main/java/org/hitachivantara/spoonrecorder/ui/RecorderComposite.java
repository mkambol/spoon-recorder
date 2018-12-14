package org.hitachivantara.spoonrecorder.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;
import org.hitachivantara.spoonrecorder.ui.layout.EventTable;

public class RecorderComposite extends Composite {
  public RecorderComposite( Composite composite, int i ) {
    super( composite, SWT.NO_BACKGROUND );
    this.setSize( 400, 380 );
    GridLayout gridLayout = new GridLayout( 3, true );

    this.setLayout( gridLayout );

    Composite left = new Composite( this, SWT.BORDER );
    left.setLayout( new FillLayout(  ) );
    left.setBackground( new Color( Display.getCurrent(), 255, 255, 255 ) );
    GridData gd = new GridData( GridData.FILL_BOTH );
    gd.horizontalSpan = 2;
    left.setLayoutData( gd );

    EventTable eventTable = new EventTable( left );
    eventTable.render();

    Composite right = new Composite( this, SWT.NONE );
    right.setLayout( new RowLayout( SWT.HORIZONTAL ) );
    GridData gdRight = new GridData( GridData.FILL_BOTH );
    right.setLayoutData( gdRight );
    Button recBtn = new Button( right, SWT.BORDER );
    recBtn.setText( "Record" );

    Button saveBtn = new Button( right, SWT.BORDER );
    saveBtn.setText( "Save" );
  }
}
