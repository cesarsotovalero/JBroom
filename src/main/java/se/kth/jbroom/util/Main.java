package se.kth.jbroom.util;

import java.util.Arrays;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        int N = sc.nextInt();
        int M = sc.nextInt();

        int[] trees = new int[N];

        for (int i = 0; i < N; i++) {
            trees[i] = sc.nextInt();
        }

        Arrays.sort(trees);

        for (int i = N-1; i >= 0; i--) {
            System.out.println(trees[i]);
        }




    }
}
