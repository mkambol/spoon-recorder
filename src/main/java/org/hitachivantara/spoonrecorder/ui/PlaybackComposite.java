package org.hitachivantara.spoonrecorder.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;
import org.hitachivantara.spoonrecorder.adapters.PlaybackSelectionAdapter;
import org.hitachivantara.spoonrecorder.listeners.OpenSelectionListener;

public class PlaybackComposite extends Composite {
  public PlaybackComposite( Composite composite, int i ) {
    super( composite, SWT.NO_BACKGROUND );
    GridLayout gridLayout = new GridLayout( 4, true );

    this.setLayout( gridLayout );

    Composite left = new Composite( this, SWT.BORDER );
    left.setLayout( new FillLayout(  ) );
    left.setBackground( new Color( Display.getCurrent(), 255, 255, 255 ) );
    GridData gd = new GridData( GridData.FILL_BOTH );
    gd.horizontalSpan = 3;
    left.setLayoutData( gd );

    Text playbackLogWindow = new Text( left, SWT.BORDER );
    playbackLogWindow.setText( "Waiting for File Upload..." );

    Composite right = new Composite( this, SWT.NONE );
    right.setLayout( new RowLayout( SWT.HORIZONTAL ) );
    GridData gdRight = new GridData( GridData.FILL_BOTH );
    right.setLayoutData( gdRight );
    
    Button uploadBtn = new Button( right, SWT.BORDER );
    uploadBtn.setText( "Upload File" );
    uploadBtn.addSelectionListener( new OpenSelectionListener( composite.getParent().getShell() ) );

    Button playbackBtn = new Button( right, SWT.BORDER );
    playbackBtn.setText( "Playback" );
    playbackBtn.addSelectionListener( new PlaybackSelectionAdapter( composite.getParent().getShell() ) );
  }
}
