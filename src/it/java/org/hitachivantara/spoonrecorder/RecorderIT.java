package org.hitachivantara.spoonrecorder;


import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.junit.Before;
import org.junit.Test;
import org.pentaho.di.core.Props;
import org.pentaho.di.core.variables.Variables;
import org.pentaho.di.ui.core.FormDataBuilder;
import org.pentaho.di.ui.core.PropsUI;

import org.pentaho.di.ui.core.widget.ColumnInfo;
import org.pentaho.di.ui.core.widget.TableView;

import java.io.IOException;
import java.nio.file.Paths;

public class RecorderIT {


  private Display display = new Display();
  private Shell shell = new Shell( display );
  private PropsUI props;
  private ModifyListener modifyListener = ( p ) -> {
  };

  @Before
  public void before() {
    if ( Props.isInitialized() ) {
      Props.getInstance().reset();
    }
    PropsUI.init( display, "" );
    props = PropsUI.getInstance();

    shell.setLayout( new FormLayout() );
  }

  @Test
  public void testRecord() {


    Button btn = addButton( shell, "button", new FormDataBuilder().top().left().result() );
    addText( shell, "text", new FormDataBuilder().top().left( btn, 200 ).result() );
    addTableView( shell, new FormDataBuilder().top( btn, 200 ).left().result() );

    try ( SWTRecorder rec = new SWTRecorder( shell, Paths.get( "/tmp/abcd.swt" ) ) ) {
      rec.open();
      shell.open();
      while ( !shell.isDisposed() ) {
        if ( !display.readAndDispatch() ) {
          display.sleep();
        }
      }
    } catch ( IOException e ) {
      e.printStackTrace();
    }


  }

  private TableView addTableView( Composite composite, FormData result ) {
    TableView tableView = new TableView( new Variables(), composite, SWT.FULL_SELECTION | SWT.MULTI | SWT.BORDER,
      new ColumnInfo[] { new ColumnInfo( "name1", ColumnInfo.COLUMN_TYPE_TEXT, false, false ) }, 5, modifyListener,
      props );
    tableView.setLayoutData( result );
    props.setLook( tableView );
    return tableView;
  }


  private Text addText( Composite composite, String content, FormData result ) {
    Text text = new Text( composite, SWT.SINGLE | SWT.LEFT | SWT.BORDER );
    text.setText( content );
    text.setLayoutData( result );
    props.setLook( text );
    return text;
  }

  private Button addButton( Composite composite, String text, FormData data ) {
    Button btn = new Button( composite, SWT.NONE );
    btn.setText( text );
    props.setLook( btn );
    btn.setLayoutData( data );
    return btn;

  }
}
