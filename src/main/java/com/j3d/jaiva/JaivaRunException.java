package com.j3d.jaiva;

import com.j3d.errors.GenericException;
import com.j3d.errors.J3DError;
import com.j3d.errors.severity.J3DInfo;
import com.jaiva.errors.JaivaException;
import com.yetnt.utils.builders.AnsiColour;

public class JaivaRunException extends GenericException implements J3DInfo {
    public JaivaRunException(JaivaException exception) {
        super(AnsiColour.remove(exception.getRawMessage()));
    }
}
