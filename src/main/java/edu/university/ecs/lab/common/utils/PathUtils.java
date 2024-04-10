package edu.university.ecs.lab.common.utils;

public class PathUtils {

    public static String getClassNameFromLocalPath(String localPath) {
        return localPath.substring(localPath.lastIndexOf("/") + 1, localPath.length() - 5);
    }

    public static String getServiceFromLocalPath(String localPath) {
        return localPath.substring(localPath.indexOf('/', 3) + 1, localPath.indexOf('/', 4));
    }

    public static String getLocalPathFromExtendedLocalPath(String extendedLocalPath) {
        return extendedLocalPath.substring(extendedLocalPath.indexOf('/', 3) + 1);
    }


}
