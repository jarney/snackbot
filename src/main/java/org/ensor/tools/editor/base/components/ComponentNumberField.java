/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ensor.tools.editor.base.components;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

/**
 *
 * @author Jon
 */
public class ComponentNumberField extends JSpinner {
    public double mDefaultValue;
    public double mMinValue;
    public double mMaxValue;
    public double mStepSize;
    public SpinnerNumberModel mModel;
    public ComponentNumberField() {
        mDefaultValue = 0.5;
        mMinValue = 0.0;
        mMaxValue = 1.0;
        mStepSize = 0.1;
        mModel = new SpinnerNumberModel(mDefaultValue, mMinValue, mMaxValue, mStepSize);
        setModel(mModel);
        this.setTransferHandler(null);
    }
    public float getDouble() {
        return (float)mModel.getNumber().doubleValue();
    }
    public int getInt() {
        return mModel.getNumber().intValue();
    }
    public void setDouble(double dv) {
        mModel.setValue(new Double(dv));
    }
    public void setDouble(float dv) {
        mModel.setValue(new Float(dv));
    }
    public void setInt(int iv) {
        mModel.setValue(new Integer(iv));
    }
    public double getDefaultValue() {
        return mDefaultValue;
    }
    public void setDefaultValue(double aDefaultValue) {
        mDefaultValue = aDefaultValue;
        mModel.setValue(mDefaultValue);
    }
    public double getMinValue() {
        return mMinValue;
    }
    public void setMinValue(double aMinValue) {
        mMinValue = aMinValue;
        mModel.setMinimum(mMinValue);
    }
    public double getMaxValue() {
        return mMaxValue;
    }
    public void setMaxValue(double aMaxValue) {
        mMaxValue = aMaxValue;
        mModel.setMaximum(mMaxValue);
    }
    public double getStepSize() {
        return mStepSize;
    }
    public void setStepSize(double aStepSize) {
        mStepSize = aStepSize;
        mModel.setStepSize(mStepSize);
    }
}
