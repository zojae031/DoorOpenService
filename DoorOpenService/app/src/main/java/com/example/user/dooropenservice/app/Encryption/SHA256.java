package com.example.user.dooropenservice.app.Encryption;

import java.security.MessageDigest;

public class SHA256
{
    public static String encrypt(String planText)
    {
        try{
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            // MessageDigest 클래스는 임의의 size의 데이터를 취해 고정 길이의 해시 값를 출력하는 단방향 해시 기능이다.
            // 송신자와 수진자만이 공유하고 있는 키와 메시지를 혼합해 해시 값을 만드는 것
            md.update(planText.getBytes());
            // update()는 지정된 바이트 데이터를 사용해 다이제스트를 갱신합니다.

            byte byteData[] = md.digest();
            // .digest()는 패딩과 같은 최종 작업을 수행하여 해시 계산을 완료한다. 이 호출이 이루어진 후에 다이제스트가 재설정된다.
            StringBuffer sb = new StringBuffer();

            for (int i = 0; i < byteData.length; i++)
            {
                sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
                // byte를 16진수 문자열(HexString)로 변환하여 문자열 추가
                /* byteData[i]에 들어있는데이터와 0xff 16진수 와 AND연산을하고 0x100 을 더한다음
                 * 다시 16진수로 만들어 나온값들중에 index 1번부터시작하여 나머지 뒤까지의 값들을
                 * Integer 클래스에 있는 toString을 사용하여 append 한다
                 */
                // 테스트 용   System.out.println("sb: " + sb);
            }

            StringBuffer hexString = new StringBuffer();

            for (int i = 0; i < byteData.length; i++)
            {
                String hex = Integer.toHexString(0xff & byteData[i]);
                // 0xff 16진수와 byteData[i]에 들어있는 데이터를 AND 연산후 String형인 hex에 넣어줌

                // 테스트용   System.out.println("before hex : "+hex);
                if(hex.length() == 1)	// hex의 길이가 1이면
                {
                    hexString.append('0'); // ex) hex값이 a이면 hexString에는 0a를 만들어 추가해주기위해 (여기서는 0만 추가하고 밑에서 hex를 추가함)
                }
                hexString.append(hex);
            }
            return hexString.toString(); // hexString을 String으로 만들어 return 해줌

        }catch(Exception e){
            e.printStackTrace();
            throw new RuntimeException();
        }
    }
}
