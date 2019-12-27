package com.example.wa007.practice;

public class RedBlackTree {
    private RedBlackTree.RedBlackNode header = new RedBlackTree.RedBlackNode((Comparable)null);
    private static RedBlackTree.RedBlackNode nullNode = new RedBlackTree.RedBlackNode((Comparable)null);
    private static final int BLACK = 1;
    private static final int RED = 0;
    private static RedBlackTree.RedBlackNode current;
    private static RedBlackTree.RedBlackNode parent;
    private static RedBlackTree.RedBlackNode grand;
    private static RedBlackTree.RedBlackNode great;

    static {
        nullNode.left = nullNode.right = nullNode;
    }

    public RedBlackTree() {
        this.header.left = this.header.right = nullNode;
    }

    private final int compare(Comparable item, RedBlackTree.RedBlackNode t) {
        return t == this.header ? 1 : item.compareTo(t.element);
    }

    public void insert(Comparable item) {
        current = parent = grand = this.header;
        nullNode.element = item;

        while(this.compare(item, current) != 0) {
            great = grand;
            grand = parent;
            parent = current;
            current = this.compare(item, current) < 0 ? current.left : current.right;
            if (current.left.color == 0 && current.right.color == 0) {
                this.handleReorient(item);
            }
        }

        if (current != nullNode) {
            throw new Error(item.toString());
        } else {
            current = new RedBlackTree.RedBlackNode(item, nullNode, nullNode);
            if (this.compare(item, parent) < 0) {
                parent.left = current;
            } else {
                parent.right = current;
            }

            this.handleReorient(item);
        }
    }

    public void remove(Comparable x) {
        throw new UnsupportedOperationException();
    }

    public Comparable findMin() {
        if (this.isEmpty()) {
            return null;
        } else {
            RedBlackTree.RedBlackNode itr;
            for(itr = this.header.right; itr.left != nullNode; itr = itr.left) {
                ;
            }

            return itr.element;
        }
    }

    public Comparable findMax() {
        if (this.isEmpty()) {
            return null;
        } else {
            RedBlackTree.RedBlackNode itr;
            for(itr = this.header.right; itr.right != nullNode; itr = itr.right) {
                ;
            }

            return itr.element;
        }
    }

    public Comparable find(Comparable x) {
        nullNode.element = x;
        current = this.header.right;

        while(true) {
            while(x.compareTo(current.element) >= 0) {
                if (x.compareTo(current.element) <= 0) {
                    if (current != nullNode) {
                        return current.element;
                    }

                    return null;
                }

                current = current.right;
            }

            current = current.left;
        }
    }

    public void makeEmpty() {
        this.header.right = nullNode;
    }

    public void printTree() {
        this.printTree(this.header.right);
    }

    private void printTree(RedBlackTree.RedBlackNode t) {
        if (t != nullNode) {
            this.printTree(t.left);
            System.out.println(t.element);
            this.printTree(t.right);
        }

    }

    public boolean isEmpty() {
        return this.header.right == nullNode;
    }

    private void handleReorient(Comparable item) {
        current.color = 0;
        current.left.color = 1;
        current.right.color = 1;
        if (parent.color == 0) {
            grand.color = 0;
            if (this.compare(item, grand) < 0 != this.compare(item, parent) < 0) {
                parent = this.rotate(item, grand);
            }

            current = this.rotate(item, great);
            current.color = 1;
        }

        this.header.right.color = 1;
    }

    private RedBlackTree.RedBlackNode rotate(Comparable item, RedBlackTree.RedBlackNode parent) {
        return this.compare(item, parent) < 0 ? (parent.left = this.compare(item, parent.left) < 0 ? rotateWithLeftChild(parent.left) : rotateWithRightChild(parent.left)) : (parent.right = this.compare(item, parent.right) < 0 ? rotateWithLeftChild(parent.right) : rotateWithRightChild(parent.right));
    }

    private static RedBlackTree.RedBlackNode rotateWithLeftChild(RedBlackTree.RedBlackNode k2) {
        RedBlackTree.RedBlackNode k1 = k2.left;
        k2.left = k1.right;
        k1.right = k2;
        return k1;
    }

    private static RedBlackTree.RedBlackNode rotateWithRightChild(RedBlackTree.RedBlackNode k1) {
        RedBlackTree.RedBlackNode k2 = k1.right;
        k1.right = k2.left;
        k2.left = k1;
        return k2;
    }

    private static class RedBlackNode {
        Comparable element;
        RedBlackTree.RedBlackNode left;
        RedBlackTree.RedBlackNode right;
        int color;

        RedBlackNode(Comparable theElement) {
            this(theElement, (RedBlackTree.RedBlackNode)null, (RedBlackTree.RedBlackNode)null);
        }

        RedBlackNode(Comparable theElement, RedBlackTree.RedBlackNode lt, RedBlackTree.RedBlackNode rt) {
            this.element = theElement;
            this.left = lt;
            this.right = rt;
            this.color = 1;
        }
    }
}
