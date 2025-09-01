package in.sk;

import in.sk.views.Welcome;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        Welcome w=new Welcome();
        do{
            w.welcomeScreen();
        }while(true);


    }
}
