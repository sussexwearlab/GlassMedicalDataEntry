/*
 * Copyright (c) 2019. Francisco Javier Ordoñez Morales, Mathias Ciliberto, Daniel Roggen
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the
 * Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package sussex.strc.wear.test.grid;

/**
 * Created by fjordonez on 09/12/15.
 */
public class CellData<I,S> {

    private final I index;
    private final S symbol;

    public CellData(I index, S symbol) {
        this.index = index;
        this.symbol = symbol;
    }

    public I getIndex() { return index; }
    public S getSymbol() { return symbol; }

    @Override
    public int hashCode() { return index.hashCode() ^ symbol.hashCode(); }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof CellData)) return false;
        CellData cellData = (CellData) o;
        return this.index.equals(cellData.getIndex()) &&
                this.symbol.equals(cellData.getSymbol());
    }

    @Override
    public String toString() {
        String ret = this.index.toString() + '-' +  this.symbol.toString();
        return ret;
    }
}