package com.finderfeed.util;

import com.finderfeed.blocks.Side;
import org.joml.Vector3d;
import org.joml.Vector3f;

public record Face(Side normal,Vector3d p1, Vector3d p2, Vector3d p3, Vector3d p4){

}
