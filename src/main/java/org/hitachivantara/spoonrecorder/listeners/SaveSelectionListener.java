package org.hitachivantara.spoonrecorder.listeners;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class SaveSelectionListener implements SelectionListener {
  Shell parent;
  public SaveSelectionListener( Shell parent ){
    this.parent = parent;
  }

  @Override
  public void widgetSelected( SelectionEvent selectionEvent ) {
    FileDialog fd = new FileDialog( parent, SWT.SAVE);
    fd.setText("Save");
    fd.setFilterPath("C:/");
    String[] filterExt = {"*.txt","*.doc", ".rtf", "*.*"};
    fd.setFilterExtensions(filterExt);
    String filePath = fd.open( );
    try {
      Files.copy( new File( "/tmp/abcd.swt" ).toPath(), new File( filePath ).toPath(), StandardCopyOption.REPLACE_EXISTING );
    } catch ( IOException ex ) {
      MessageBox errorBox = new MessageBox( parent, SWT.ICON_ERROR | SWT.OK );
      errorBox.setMessage( "File save error, could not save file" );
      int response = errorBox.open();
    }
  }

  @Override
  public void widgetDefaultSelected( SelectionEvent selectionEvent ) {

  }
}
