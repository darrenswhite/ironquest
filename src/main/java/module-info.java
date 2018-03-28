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
	exports com.darrenswhite.rs.ironquest.quest;
	opens com.darrenswhite.rs.ironquest;
	opens com.darrenswhite.rs.ironquest.quest;
}