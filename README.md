# Spoon Recorder #
_A Kettle Plugin which adds record / playback capability for purposes
 of automating test sequences._


## Overview ##

In "record" mode, this plugin writes UI events in Spoon to a structured file.  Play mode
can then run through the same sequence of events automatically.

"Record" mode treats double-clicks within a text area or table as a "verify" event, and captures
all text in the control at that point of the sequence.  Playback will then fail if the verified
text does not match.

The plugin is loaded as a kettle lifecycle listener, initializing on startup.  It's packaged as an
OSGi bundle and can be installed by dropping into the karaf/deploy folder.

The primary classes in this project are
  * `SWTRecorder` - handles recording of events, saving to file.
  * `SWTPlayback` - Reads the sequence file and performs each event. 


## Technical Challenges ##

1. SWT does not have a top level hook to capture and respond to widget events.  In order
   to dynamically respond to events in a changing UI, this project has a `SWTTreeWatcher` class
   which continuously walks the composite trees in each shell.
   
2. There is not a unique identifier for widgets.  In order to reference widgets within a
   playback file, the plugin requires some method of identification.
   
   `WidgetKey` is an imperfect workaround.  `WidgetKey` objects contain the text, classname, and index
   of a UI control, along with its _parent widget key_.   The key is not guaranteed unique.  For example, 
   controls can be dynamically added/removed, which can change index over time.  Looking for ways to 
   tighten up the key.
   
3.  The SWT API does not lend itself to general purposes retrieving and setting of values within
    controls.  Reflection is used as a dark-magic workaround.
    
    