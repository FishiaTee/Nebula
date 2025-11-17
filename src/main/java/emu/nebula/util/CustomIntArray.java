package emu.nebula.util;

import it.unimi.dsi.fastutil.ints.IntArrayList;

public class CustomIntArray extends IntArrayList {
    private static final long serialVersionUID = -3887905394107667439L;
    
    public CustomIntArray() {
        super();
    }
    
    public void add(int index, int value) {
        while (index > this.size()) {
            super.add(0);
        }
        
        super.add(index, value);
    }
    
    @Override
    public boolean add(int value) {
        for (int i = 0; i < this.size(); i++) {
            if (this.getInt(i) == 0) {
                this.set(i, value);
                return true;
            }
        }
        
        return super.add(value);
    }
    
    /**
     * Returns the amount of non-zero values in this array
     */
    public int getValueCount() {
        int realSize = 0;
        
        for (int i = 0; i < super.size(); i++) {
            if (this.getInt(i) != 0) {
                realSize++;
            }
        }
        
        return realSize;
    }
}
