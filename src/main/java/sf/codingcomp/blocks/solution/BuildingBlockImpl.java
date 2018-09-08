package sf.codingcomp.blocks.solution;

import java.util.Iterator;

import sf.codingcomp.blocks.BuildingBlock;
import sf.codingcomp.blocks.CircularReferenceException;

import javax.swing.text.html.HTMLDocument;

public class BuildingBlockImpl implements BuildingBlock {

    BuildingBlock above, below;

    @Override
    public Iterator<BuildingBlock> iterator() {
        BuildingBlock temp = this;
        while (temp.findBlockUnder() != null) {
            temp = temp.findBlockUnder();
        }
        return new BuildingBlockIterator(temp);
    }

    @Override
    public void stackOver(BuildingBlock b) {
        if (below != b) {
            // Test for circularness
            BuildingBlock curr = above;
            while (curr != null) {
                if (curr == b) {
                    throw new CircularReferenceException();
                }
                curr = curr.findBlockOver();
            }

            BuildingBlock oldBelow = below;
            below = null;
            if (oldBelow != null) {
                oldBelow.stackUnder(null);
            }
            below = b;
            if (b != null) {
                b.stackUnder(this);
            }
        }
    }

    @Override
    public void stackUnder(BuildingBlock b) {
        if (above != b) {
            // Test for circularness
            BuildingBlock curr = below;
            while (curr != null) {
                if (curr == b) {
                    throw new CircularReferenceException();
                }
                curr = curr.findBlockUnder();
            }

            BuildingBlock oldAbove = above;
            above = null;
            if (oldAbove != null) {
                oldAbove.stackOver(null);
            }
            above = b;
            if (b != null) {
                b.stackOver(this);
            }
        }
    }

    @Override
    public BuildingBlock findBlockUnder() {
        return below;
    }

    @Override
    public BuildingBlock findBlockOver() {
        return above;
    }

    private class BuildingBlockIterator implements Iterator<BuildingBlock> {
        BuildingBlock curr;
        boolean removed;

        public BuildingBlockIterator(BuildingBlock b) {
            curr = b;
            removed = true;
        }

        @Override
        public boolean hasNext() {
            return (curr != null);
        }

        @Override
        public BuildingBlock next() {
            removed = false;
            BuildingBlock temp = curr;
            curr = curr.findBlockOver();
            return temp;
        }

        public void remove() {
            BuildingBlock belowCurr = curr.findBlockUnder();
            if (removed == true) {
                throw new IllegalStateException();
            }
            removed = true;
            if (belowCurr.findBlockUnder() == null) {
                curr.stackOver(null);
            } else {
                curr.stackOver(belowCurr.findBlockUnder());
            }
        }
    }
}
