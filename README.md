MinimalBible
============

A Bible app for android designed to be more focused in both UI and feature set.

Project Outline
===============

This outline is intended to show the tasks needing to be accomplished, and give an idea of when releases will happen.

Core:
-----
These are the tasks that will need to be accomplished before the 1.0 major release.

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

**Release v.1 to Play store**

* * *

* Search
    * UI design finalized (integration in Bible Viewer, separate activity?)
    * Search functionality implemented
        * Get Lucene search working (included in JSword)
        * Tweak search (fuzzy? Lord -> LORD? Are we actually getting results we want?)

**Release v.2**

* * *

* Download Manager
    * Download manager can fetch commentaries

* Footnotes/Commentaries
    * UI design finalized
        * Frame on bottom of Bible Viewer a la http://blog.neteril.org/blog/2013/10/10/framelayout-your-best-ui-friend/ ?
        * Switch between footnotes/commentaries by swiping on panel?
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

* Home screen
    * Allow access to settings, download manager, and Bible Viewer

**Release v1.0**  
**Party!**

* * *
