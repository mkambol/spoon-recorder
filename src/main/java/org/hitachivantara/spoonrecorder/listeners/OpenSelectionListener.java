package org.hitachivantara.spoonrecorder.listeners;

import org.apache.commons.lang.StringUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import java.io.FileNotFoundException;

public class OpenSelectionListener implements SelectionListener {
  private Shell parent;
  private String filePath;
  private Text playbackWindow;

  public OpenSelectionListener( Shell parent, Text playbackWindow ) {
    this.parent = parent;
    this.playbackWindow = playbackWindow;
  }

  @Override
  public void widgetSelected( SelectionEvent selectionEvent ) {
    FileDialog fd = new FileDialog( parent, SWT.OPEN );
    fd.setText("Open");
    fd.setFilterPath("C:/");
    String[] filterExt = {"*.txt","*.doc", ".rtf", "*.*"};
    fd.setFilterExtensions(filterExt);
    filePath = fd.open( );
    playbackWindow.append( "\nLoaded file - " + filePath );
  }

  @Override
  public void widgetDefaultSelected( SelectionEvent selectionEvent ) {

  }

  public String getFilePath() throws FileNotFoundException {
    if ( StringUtils.isEmpty( filePath ) ) throw new FileNotFoundException( "No File Chosen" );

    return filePath;
  }
}
