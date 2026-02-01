package org.jivesoftware.spark.plugin.battleship.logic;

public class ShipModel {
    private final int[] _ship;

    public ShipModel(int size) {
        _ship = new int[size];
    }

    public void setBomb() {
        for (int i = 0; i < _ship.length; i++) {
            if (_ship[i] == 0) {
                _ship[i] = 1;
                break;
            }
        }
    }

    public boolean isDestroyed() {
        boolean dest = true;
        for (int i = 0; i < _ship.length && dest; i++) {
            dest = _ship[i] != 0;
        }
        return dest;
    }

    public int getSize() {
        return _ship.length;
    }
}
