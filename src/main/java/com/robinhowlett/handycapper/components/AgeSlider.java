package com.robinhowlett.handycapper.components;

import org.controlsfx.control.RangeSlider;

import javafx.geometry.Insets;
import javafx.scene.layout.AnchorPane;
import javafx.util.StringConverter;

public class AgeSlider extends RangeSlider {

    public static final int LOW_VALUE = 1;
    public static final int HIGH_VALUE = 5;

    public AgeSlider() {
        super(LOW_VALUE, HIGH_VALUE, LOW_VALUE, HIGH_VALUE);

        setShowTickMarks(true);
        setShowTickLabels(true);
        setMajorTickUnit(1);
        setMinorTickCount(0);
        setSnapToTicks(true);

        StringConverter<Number> stringConverter = new StringConverter<Number>() {
            @Override
            public String toString(Number object) {
                if (object instanceof Double) {
                    if (object.doubleValue() == 5) {
                        return "Older";
                    } else if (object.doubleValue() == 1) {
                        return "Any";
                    } else {
                        return String.valueOf(object.intValue());
                    }
                }
                return "N/A";
            }

            @Override
            public Double fromString(String string) {
                return null;
            }
        };

        setLabelFormatter(stringConverter);
        setPadding(new Insets(5,5,0,5));

        AnchorPane.setTopAnchor(this, 0d);
        AnchorPane.setBottomAnchor(this, 0d);
        AnchorPane.setRightAnchor(this, 0d);
        AnchorPane.setLeftAnchor(this, 0d);
    }
}
