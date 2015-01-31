/**@@@+++@@@@******************************************************************
**
** Microsoft (r) PlayReady (r)
** Copyright (c) Microsoft Corporation. All rights reserved.
**
***@@@---@@@@******************************************************************
*/

package com.example.mediaplayersample.subtitle;

// internal use only
enum HtmlColor
{
    red(0xFF0000),
    crimson(0xDC143C),
    firebrick(0xB22222),
    maroon(0x800000),
    darkred(0x8B0000),
    brown(0xA52A2A),
    sienna(0xA0522D),
    saddlebrown(0x8B4513),
    indianred(0xCD5C5C),
    rosybrown(0xBC8F8F),
    lightcoral(0xF08080),
    salmon(0xFA8072),
    darksalmon(0xE9967A),
    coral(0xFF7F50),
    tomato(0xFF6347),
    sandybrown(0xF4A460),
    lightsalmon(0xFFA07A),
    peru(0xCD853F),
    chocolate(0xD2691E),
    orangered(0xFF4500),
    orange(0xFFA500),
    darkorange(0xFF8C00),
    tan(0xD2B48C),
    peachpuff(0xFFDAB9),
    bisque(0xFFE4C4),
    moccasin(0xFFE4B5),
    navajowhite(0xFFDEAD),
    wheat(0xF5DEB3),
    burlywood(0xDEB887),
    darkgoldenrod(0xB8860B),
    goldenrod(0xDAA520),
    gold(0xFFD700),
    yellow(0xFFFF00),
    lightgoldenrodyellow(0xFAFAD2),
    palegoldenrod(0xEEE8AA),
    khaki(0xF0E68C),
    darkkhaki(0xBDB76B),
    lawngreen(0x7CFC00),
    greenyellow(0xADFF2F),
    chartreuse(0x7FFF00),
    lime(0x00FF00),
    limegreen(0x32CD32),
    yellowgreen(0x9ACD32),
    olive(0x808000),
    olivedrab(0x6B8E23),
    darkolivegreen(0x556B2F),
    forestgreen(0x228B22),
    darkgreen(0x006400),
    green(0x008000),
    seagreen(0x2E8B57),
    mediumseagreen(0x3CB371),
    darkseagreen(0x8FBC8F),
    lightgreen(0x90EE90),
    palegreen(0x98FB98),
    springgreen(0x00FF7F),
    mediumspringgreen(0x00FA9A),
    teal(0x008080),
    darkcyan(0x008B8B),
    lightseagreen(0x20B2AA),
    mediumaquamarine(0x66CDAA),
    cadetblue(0x5F9EA0),
    steelblue(0x4682B4),
    aquamarine(0x7FFFD4),
    powderblue(0xB0E0E6),
    paleturquoise(0xAFEEEE),
    lightblue(0xADD8E6),
    lightsteelblue(0xB0C4DE),
    skyblue(0x87CEEB),
    lightskyblue(0x87CEFA),
    mediumturquoise(0x48D1CC),
    turquoise(0x40E0D0),
    darkturquoise(0x00CED1),
    aqua(0x00FFFF),
    cyan(0x00FFFF),
    deepskyblue(0x00BFFF),
    dodgerblue(0x1E90FF),
    cornflowerblue(0x6495ED),
    royalblue(0x4169E1),
    blue(0x0000FF),
    mediumblue(0x0000CD),
    navy(0x000080),
    darkblue(0x00008B),
    midnightblue(0x191970),
    darkslateblue(0x483D8B),
    slateblue(0x6A5ACD),
    mediumslateblue(0x7B68EE),
    mediumpurple(0x9370DB),
    darkorchid(0x9932CC),
    darkviolet(0x9400D3),
    blueviolet(0x8A2BE2),
    mediumorchid(0xBA55D3),
    plum(0xDDA0DD),
    lavender(0xE6E6FA),
    thistle(0xD8BFD8),
    orchid(0xDA70D6),
    violet(0xEE82EE),
    indigo(0x4B0082),
    darkmagenta(0x8B008B),
    purple(0x800080),
    mediumvioletred(0xC71585),
    deeppink(0xFF1493),
    fuchsia(0xFF00FF),
    magenta(0xFF00FF),
    hotpink(0xFF69B4),
    palevioletred(0xDB7093),
    lightpink(0xFFB6C1),
    pink(0xFFC0CB),
    mistyrose(0xFFE4E1),
    blanchedalmond(0xFFEBCD),
    lightyellow(0xFFFFE0),
    cornsilk(0xFFF8DC),
    antiquewhite(0xFAEBD7),
    papayawhip(0xFFEFD5),
    lemonchiffon(0xFFFACD),
    beige(0xF5F5DC),
    linen(0xFAF0E6),
    oldlace(0xFDF5E6),
    lightcyan(0xE0FFFF),
    aliceblue(0xF0F8FF),
    whitesmoke(0xF5F5F5),
    lavenderblush(0xFFF0F5),
    floralwhite(0xFFFAF0),
    mintcream(0xF5FFFA),
    ghostwhite(0xF8F8FF),
    honeydew(0xF0FFF0),
    seashell(0xFFF5EE),
    ivory(0xFFFFF0),
    azure(0xF0FFFF),
    snow(0xFFFAFA),
    white(0xFFFFFF),
    gainsboro(0xDCDCDC),
    lightgrey(0xD3D3D3),
    silver(0xC0C0C0),
    darkgray(0xA9A9A9),
    lightslategray(0x778899),
    slategray(0x708090),
    gray(0x808080),
    dimgray(0x696969),
    darkslategray(0x2F4F4F),
    black(0x000000);

    private final  int mData;
    HtmlColor(int data)
    {
        mData = data;
    }

    public int getInt()
    {
        return mData;
    }

    public static HtmlColor fromInt(final int data)
    {
        for (HtmlColor method : HtmlColor.values())
        {
            if (method.mData == data)
            {
                return method;
            }
        }

        return null;
    }

    public static HtmlColor fromString(final String data)
    {
        for (HtmlColor method : HtmlColor.values())
        {
            if ( method.toString().equals(data))
            {
                return method;
            }
        }

        return null;
    }

}
