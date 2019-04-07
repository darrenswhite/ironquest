package com.darrenswhite.rs.ironquest.player;

/**
 * @author Darren S. White
 */
public enum SkillType {

  ARTISAN,
  COMBAT,
  ELITE,
  GATHERING,
  SUPPORT;

  @Override
  public String toString() {
    char first = name().toUpperCase().charAt(0);
    String remaining = name().substring(1).toLowerCase().replace('_', ' ');

    return first + remaining;
  }
}
