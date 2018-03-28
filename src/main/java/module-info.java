module IronQuest {
	requires gson;
	requires javafx.graphics;
	requires java.logging;
	requires javafx.controls;
	requires java.desktop;
	requires commons.csv;
	requires javafx.swing;
	requires java.sql;
	exports com.darrenswhite.rs.ironquest;
    exports com.darrenswhite.rs.ironquest.action;
    exports com.darrenswhite.rs.ironquest.player;
	exports com.darrenswhite.rs.ironquest.quest;
    exports com.darrenswhite.rs.ironquest.quest.requirement;
    opens com.darrenswhite.rs.ironquest;
    opens com.darrenswhite.rs.ironquest.action;
    opens com.darrenswhite.rs.ironquest.player;
    opens com.darrenswhite.rs.ironquest.quest;
    opens com.darrenswhite.rs.ironquest.quest.requirement;
}