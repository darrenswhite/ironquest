package com.darrenswhite.rs.ironquest.quest.requirement;

import com.darrenswhite.rs.ironquest.player.Player;

/**
 * @author Darren White
 */
public class QuestPointsRequirement extends Requirement {

    private final int qp;

    public QuestPointsRequirement(int qp) {
        this(false, false, qp);
    }

    public QuestPointsRequirement(boolean ironman, boolean recommended,
                                  int qp) {
        super(ironman, recommended);
        this.qp = qp;
    }

    @Override
    public boolean isOther() {
        return true;
    }

    @Override
    protected boolean test(Player p) {
        return p.getQuestPoints() >= qp;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(qp);
        sb.append(" Quest Points");
        if (isIronman()) {
            sb.append(" (Ironman)");
        }
        if (isRecommended()) {
            sb.append(" (Recommended)");
        }
        return sb.toString();
    }
}