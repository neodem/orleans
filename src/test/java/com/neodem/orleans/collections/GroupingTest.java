package com.neodem.orleans.collections;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Created by Vincent Fumo (neodem@gmail.com)
 * Created on 12/28/19
 */
public class GroupingTest {
    private Grouping grouping;

    @BeforeEach
    void setUp() {
        grouping = new Grouping(1, 2, 3);
    }

    @AfterEach
    void tearDown() {
        grouping = null;
    }

//    @Test
//    void canFitIntoShouldNotAllowDiffTypes() {
//        Grouping test = new Grouping("1", "2");
//        test.canFitInto(grouping);
//    }

    @Test
    void equalsShouldFailOnDiffSize() {
        Grouping test = new Grouping(1);
        assertThat(test).isNotEqualTo(grouping);
    }

    @Test
    void equalsShouldFailOnDiffSize2() {
        Grouping test = new Grouping(1,2,3,4);
        assertThat(test).isNotEqualTo(grouping);
    }

    @Test
    void equalsShouldWork() {
        Grouping test = new Grouping(1,2,3);
        assertThat(test).isEqualTo(grouping);
    }

    @Test
    void equalsShouldNotMindOrder() {
        Grouping test = new Grouping(3,2,1);
        assertThat(test).isEqualTo(grouping);
    }

    @Test
    void hashCodeShouldNotMindOrder() {
        Grouping test = new Grouping(3,2,1);
        assertThat(test.hashCode()).isEqualTo(grouping.hashCode());
    }

    @Test
    void canFitIntoShouldWorkAsExpected() {
        Grouping test = new Grouping(1, 2);
        boolean result = test.canFitInto(grouping);
        assertThat(result).isTrue();
    }

    @Test
    void canFitIntoShouldWorkAsExpected2() {
        Grouping test = new Grouping(1);
        boolean result = test.canFitInto(grouping);
        assertThat(result).isTrue();
    }

    @Test
    void canFitIntoShouldWorkAsExpected3() {
        Grouping test = new Grouping(1, 2, 3);
        boolean result = test.canFitInto(grouping);
        assertThat(result).isTrue();
    }

    @Test
    void canFitIntoShouldFailIfOneValueIsDifferent() {
        Grouping test = new Grouping(1, 2, 4);
        boolean result = test.canFitInto(grouping);
        assertThat(result).isFalse();
    }

    @Test
    void canFitIntoShouldFailIfOneValueIsDifferent2() {
        Grouping test = new Grouping(1, 2, 3, 4);
        boolean result = test.canFitInto(grouping);
        assertThat(result).isFalse();
    }

    @Test
    void groupingShouldOnlyAllowNonEmptyContruction() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new Grouping());
    }
}
