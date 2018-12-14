package org.hitachivantara.spoonrecorder.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.*;
import org.pentaho.di.ui.core.PropsUI;

public class RecorderShell {
  private Display display;

  public RecorderShell( Display display ) {
    this.display = display;
  }

  public void render() {
    Shell recorderShell = new Shell( display );
    recorderShell.setLayout( renderLayout() );

    renderTabFolder( recorderShell );

    recorderShell.setLocation( 10, 10 );
    recorderShell.setSize( 640, 400 );
    recorderShell.open();
  }

  private TabFolder renderTabFolder( Composite c ) {
    TabFolder tf = new TabFolder( c, SWT.BORDER );

    TabItem recordTab = new TabItem( tf, SWT.BORDER );
    recordTab.setText( "Recorder" );
    recordTab.setControl( new RecorderComposite( tf, SWT.BORDER ) );

    TabItem playbackTab = new TabItem( tf, SWT.BORDER );
    playbackTab.setText( "Playback" );
    playbackTab.setControl( new PlaybackComposite( tf, SWT.BORDER ) );

    return tf;
  }

  private FillLayout renderLayout() {
    FillLayout fl = new FillLayout(  );
    return fl;
  }
}
