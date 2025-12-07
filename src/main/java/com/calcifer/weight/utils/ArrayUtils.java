package com.calcifer.weight.utils;

public class ArrayUtils {

    /**
     * 反转数组
     *
     * @param array 需要反转的数组
     * @param <T>   数组元素的类型
     */
    public static <T> void reverse(T[] array) {
        int left = 0;
        int right = array.length - 1;

        while (left < right) {
            // 交换元素
            T temp = array[left];
            array[left] = array[right];
            array[right] = temp;

            left++;
            right--;
        }
    }

    // 测试反转
    public static void main(String[] args) {
        Integer[] arr = {1, 2, 3, 4, 5};
        System.out.println("原数组: ");
        for (int num : arr) {
            System.out.print(num + " ");
        }

        reverse(arr);

        System.out.println("\n反转后的数组: ");
        for (int num : arr) {
            System.out.print(num + " ");
        }
    }
}

