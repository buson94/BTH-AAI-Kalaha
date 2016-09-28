/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ai;

/**
 *
 * @author jt-1
 */
public class PruningManager {
    private int alpha = Integer.MIN_VALUE;
    private int beta = Integer.MAX_VALUE;
    
    public boolean pruneBranch(int value, boolean isMaxNode) {
        if (isMaxNode) {
            alpha = value > alpha ? value : alpha;
            return value > beta;
        }
        else {
            beta = value < beta ? value : beta;
            return value < alpha;
        }
    }
}
