package de.lucky.datadrivenimgui.config;

import java.util.List;

public class ElementConfig {
    // Common fields
    public String type;    // e.g., "text", "button", "checkbox", "sliderInt", "sliderFloat", "inputText", "combo", "colorEdit3", "treeNode"
    public String label;   // Used for buttons, inputText, slider labels, etc.
    public String content; // For text or tree node content

    // For checkbox
    public Boolean checked;

    // For sliderInt/sliderFloat
    public Float min;
    public Float max;
    public Float defaultValue;

    // For inputText
    public String defaultText;

    // For combo
    public List<String> options;

    // For colorEdit3
    public List<Float> defaultColor; // Expect at least three floats (RGB)

    //For buttons and checkboxes to run commands
    public String commandToRun; // The command to run
}
