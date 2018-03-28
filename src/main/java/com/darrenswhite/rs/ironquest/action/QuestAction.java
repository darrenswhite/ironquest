package com.darrenswhite.rs.ironquest.action;

import com.darrenswhite.rs.ironquest.gui.QuestDetail;
import com.darrenswhite.rs.ironquest.player.Player;
import com.darrenswhite.rs.ironquest.quest.Quest;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;

import java.util.Objects;

/**
 * @author Darren White
 */
public class QuestAction extends Action {

    /**
     * The Quest object
     */
    private final Quest quest;

    /**
     * Creates a new QuestAction
     *
     * @param player The Player
     * @param quest  The Quest
     */
    public QuestAction(Player player, Quest quest) {
        super(player);
        this.quest = Objects.requireNonNull(quest);
    }

    @Override
    public String getMessage() {
        return quest.getDisplayName();
    }

    /**
     * Gets the Quest object
     *
     * @return A Quest
     */
    public Quest getQuest() {
        return quest;
    }

    @Override
    public void onClick(Scene scene, MouseEvent e) {
        super.onClick(scene, e);

        if (e.getClickCount() != 2) {
            return;
        }

        QuestDetail questDetail = new QuestDetail(scene.getWindow(), quest);

        questDetail.sizeToScene();
        questDetail.showAndWait();
    }
}