package frc.util;

//Combines SimpleProp and some BotMath methods

public class PropMath{

    private double kFB, kSP, kPB, kDB, outMn, outMx;

    public PropMath(double _kSP, double _kPB, double _kDB,
    /*             */ double _outMn, double _outMx){
        kSP = _kSP;  kPB = _kPB;  kDB = _kDB;
        outMn = _outMn;  outMx = _outMx;
    }

    public PropMath(double[] parm ){
        kSP = parm[0];
        kPB = parm[1];
        kDB = parm[2];
        outMn = parm[3];
        outMx = parm[4];
    }

    // Calculate a simple proportional responce.
    public double calcProp(double inFB, boolean prnt){
        double err = inFB - kSP;   //error
        if(prnt) System.out.println("Mn- " + outMn + "  Mx- " + outMx);
        if(Math.abs(err) < kDB || kPB == 0.0) return  0.0; //In deadband or PB is 0
        err /= kPB;  //else calc proportional, neg. else pos.
        if(prnt) System.out.print("err1- " + err);
        err = err < 0 ?
        Span(err, -1.0, 0.0, -outMx, -outMn, true, 0) : //Neg.
        Span(err, 0.0, 1.0, outMn, outMx, true, 0);     //else Pos.
        if(prnt) System.out.println("  err2- " + err);
        return err;
    }

    // Set k's
    public void setSP(double _kSP) { kSP = _kSP; }
    public void setPB(double _kPB) { kPB = _kPB; }
    public void setDB(double _kDB) { kDB = _kDB; }
    public void setOutMn(double _outMn) { outMn = _outMn; }
    public void setOutMx(double _outMx) { outMx = _outMx; }

    // Get k's
    public double getSP() { return kSP; }
    public double getPB() { return kPB; }
    public double getDB() { return kDB; }
    public double getOutMn() { return outMn; }
    public double getOutMx() { return outMx; }

        //------ Span methods -----------
    // span in between inLo & inHi to outLo & outHi
    // -- inLo must be lower than inHi
    // -- inDelta (inhi - inLo) MUST NOT BE 0. Returns outLo
    // -- outLo asso w/ inLo & outHi asso with inHi
    // -- clamp - limit between outLo & outHi
    // -- sqrt - apply sqrt root to input then span
    public static double Span(double inVal, double inLo, double inHi,
                                            double outLo, double outHi,
                                            boolean clamp, int app){
        
        if(inHi == inLo) return 0.0;    //Invalid values
        if(outHi == outLo) return outLo;

        double tmp = (inVal - inLo) / (inHi - inLo);    //Calc in ratio
        double outDelta = ( outHi - outLo );

        switch(app){
            case 0: //Linear (Do nothing)
            break;            
            case 1: //Square
                tmp = ( tmp < 0 ? -1.0 : 1.0 ) * Math.sqrt( Math.abs( tmp )) * outDelta;
            break;            
            case 2: //Square root
                tmp = ( tmp < 0 ? -1.0 : 1.0 ) * Math.sqrt( Math.abs( tmp )) * outDelta;
            break;            
            default:
            break;            
        }
        tmp = (tmp * outDelta) + outLo; //Ratio time out range + offset

        if( clamp ) tmp = Clamp(tmp, outLo, outHi);

        return tmp;
    }

    // inVal is limited between outLo & outHi
    public static double Clamp( double inVal, double val1, double val2){
        double tmp = Math.min(val1, val2);
        if( inVal < tmp ){
            return tmp;
        }

        tmp = Math.max(val1, val2);
        if( inVal > tmp ){
            return tmp;
        }

        return inVal;
    }

    public static double normalizeTo180( double inAngle){
        double tmpD = inAngle % 360.0;
        if( tmpD < -180.0 ){
            tmpD += 360.0;
        }else if(tmpD > 180){
            tmpD -= 360;
        }
        return tmpD;
    }

    public static double SegLine(double inVal, double arInOut[][] ){
        int arLen = arInOut[0].length - 1;
        if(inVal < arInOut[0][0]) return arInOut[1][0];
        if(inVal > arInOut[0][arLen]) return arInOut[1][arLen];
        int x = 0;
        while( ++x < arLen && inVal >= arInOut[0][x] );
        return Span(inVal, arInOut[0][x-1], arInOut[0][x],
                                   arInOut[1][x-1], arInOut[1][x], true, 0);
    }
}