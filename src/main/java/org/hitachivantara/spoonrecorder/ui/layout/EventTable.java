package org.hitachivantara.spoonrecorder.ui.layout;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

public class EventTable {
  Table eventTable;

  public EventTable( Composite parent ){
    eventTable = new Table( parent, SWT.SINGLE );
  }

  public void render() {
    TableColumn keyColumn = new TableColumn( eventTable, SWT.CENTER );
    keyColumn.setText( "KEY" );
    keyColumn.setWidth( 80 );
    keyColumn.setResizable( true );

    TableColumn eventNameColumn = new TableColumn( eventTable, SWT.CENTER );
    eventNameColumn.setText( "NAME" );
    eventNameColumn.setWidth( 80 );
    eventNameColumn.setResizable( true );

    TableColumn widgetColumn = new TableColumn( eventTable, SWT.CENTER );
    widgetColumn.setText( "WIDGET" );
    widgetColumn.setWidth( 80 );
    widgetColumn.setResizable( true );

    TableColumn textColumn = new TableColumn( eventTable, SWT.CENTER );
    textColumn.setText( "TEXT" );
    textColumn.setWidth( 80 );
    textColumn.setResizable( true );

    TableColumn eventHashColumn = new TableColumn( eventTable, SWT.CENTER );
    eventHashColumn.setText( "EVENT" );
    eventHashColumn.setWidth( 80 );
    eventHashColumn.setResizable( true );
    eventTable.setHeaderVisible( true );
  }
}
