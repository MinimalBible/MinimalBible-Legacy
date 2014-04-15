MinimalBible
============

A Bible app for android designed to be more focused in both UI and feature set.

Project Outline
===============

This outline is intended to show the tasks needing to be accomplished, and give an idea of when releases will happen.

Core:
-----
These are the tasks that will need to be accomplished before the 1.0 major release.

* Project setup
	* Add the appcompat project to the Git repository

* Integration with JSword
    * Build JSword
    * Distribute an Android binary that contains JSword and 3rd party libraries

* Download Manager
    * Bible browser
    * Can download Bibles
    * Can remove Bibles
    * Generate search indexes for Bibles

* Bible Viewer
    * UI design finalized
         * Use Immersive mode for 4.4+?
         * Panels for footnotes, commentary?
         * Navigation drawer for books?
         * What gestures should be used? (Swipe left/right for chapter search?)
         * How to get to Download Manager / some form of home page?
    * Navigation of books working
    * Can display Bible text
        * Time from launch to viewing text under 5s. Ideally, under 3s. as well.
    * Can use navigation drawer to open a book
    * Infinite scroll between chapters
        * Research how to accomplish infinite scroll
        * Implement infinite scroll
    * Red letter enabled

**Release v.1 to Play store**

* * *

* Search
    * UI design finalized (integration in Bible Viewer, separate activity?)
    * Search functionality implemented
        * Get Lucene search working (included in JSword)
        * Tweak search (fuzzy? Lord -> LORD? Are we actually getting results we want?)
    * Search history recorded
        * Record when search took place?

**Release v.2**

* * *

* Download Manager
    * Download manager can fetch commentaries

* Footnotes/Commentaries
    * UI design finalized
        * Frame on bottom of Bible Viewer a la [this](http://blog.neteril.org/blog/2013/10/10/framelayout-your-best-ui-friend/)?
        * Switch between footnotes/commentaries by swiping on panel?
        * Right-side nav drawer like FB?
        * Can we synchronize scroll between commentaries/footnotes?
        * Should Bible search also search commentaries?
    * Implement/Show commentaries/footnotes
    * Synchronize scrolling Bible to footnotes/commentaries
        * Is this possible?
        * Implement it!
    * Clicking on note in text opens commentary

**Release v.3**

* * *

* Settings Manager
    * Night mode?
    * Automatic night mode?
    * Text font/size
    * Clear searches?
    * Disable red-letter?
    * Send feedback

* Home screen
    * Allow access to settings, download manager, and Bible Viewer

**Release v1.0**  
**Party!**

* * *

Feature Addition
----------------

These are features I want to add, but are not considered part of the "core" product. Many (most) are necessary features of a modern app, but follow after the first major release.

### Usage statistics
* Include usage statistics?
    * Only send statistics on WiFi?
    * Disable by default? Prompt user?

### Sharing
* UI Design finalized
    * Click on text to select it, then share?
    * Long-click text to share?
    * Share currently active text?
    * Dialog to select what range of text is included?
    * Share commentary/footnotes?
* Intent filter created to share via FB, email, etc.
* Settings
    * Share link to app alongside text? Allow disabling?

* * *

### Highlighting
* UI Design finalized
    * Click on text to select like share?
    * Highlight colors / custom colors?
    * Multiple highlight colors?
    * Remove highlights?

* Database backing
    * First feature to need a database! Success!
    * How to store sections of highlighted material?
    * Store start/end range, along with highlight color?
    * Store when highlight was created?

* Bible Viewer
    * How to show highlights?
        * More specifically, how does showing highlights impact performance on start?
        * Show text first, then highlights after loaded?

* * *

### Notes
* UI Design finalized
    * Likely very similar to highlighting

* Database backing
    * Likely very similar to highlighting
    * Store note instead of highlight color?
    * Store when note was created / updated?

* Bible Viewer
    * Same concerns as highlighting, how does it impact speed?
    * How do we display notes? Highlighting changes background color, have a separate link for our notes?
    * Do notes get added to a panel on bottom? Can user browse notes?

* Home Screen
    * Notes browser

* * *

Plenty to get done!
