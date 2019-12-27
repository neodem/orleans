package com.neodem.orleans.service;

import com.neodem.orleans.objects.GameVersion;
import com.neodem.orleans.objects.GameState;

import java.util.List;

/**
 * Created by Vincent Fumo (neodem@gmail.com)
 * Created on 12/26/19
 */
public interface GameMaster {
    GameState makeGame(String gameId, List<String> playerNames, GameVersion gameVersion);
}
