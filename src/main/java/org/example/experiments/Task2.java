package org.example.experiments;

import java.util.ArrayList;
import java.util.List;

public class Task2 {

    public ListNode addTwoNumbers(ListNode l1, ListNode l2) {
        List<Integer> list1 = new ArrayList<>();
        List<Integer> list2 = new ArrayList<>();
        List<Integer> resultList = new ArrayList<>();

        ListNode currentL1 = l1;
        while (currentL1 != null) {
            list1.add(currentL1.val);
            currentL1 = currentL1.next;
        }

        ListNode currentL2 = l2;
        while (currentL2 != null) {
            list2.add(currentL2.val);
            currentL2 = currentL2.next;
        }









        /*ListNode result = null;
        ListNode currentResult = null;

        ListNode currentL1 = l1;
        ListNode currentL2 = l2;
        boolean isTransfer;
        while (true) {
            int valRes;
            ListNode nextResult;

            if (currentL1 != null && currentL2 != null) {
                valRes = currentL1.val + currentL2.val;
                if (result == null) {
                   // currentResult = new ListNode(valRes);
                    result = new ListNode(valRes);
                } else if (result.next == null) {
                    currentResult = new ListNode(valRes);
                    result.next = currentResult;
                }
            }

            currentL1 = currentL1.next;
            currentL2 = currentL2.next;
        }*/

        return null;
    }

    static class ListNode {
      int val;
      ListNode next;
      ListNode() {}
      ListNode(int val) { this.val = val; }
      ListNode(int val, ListNode next) { this.val = val; this.next = next; }
  }

}
