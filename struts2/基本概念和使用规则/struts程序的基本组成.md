1.struts�����WebӦ�õ���ɣ�

-Ӧ�ø�Ŀ¼
 |__.jsp��.html
 |
 |__WEB-INF/
    |
    |__web.xml
    |
    |__lib/(��������˸�����������⣬��������Ҫ�ľ���Struts�������
    |
    |__classes/
       |
       |__struts.xml��Struts�����ļ�������涨������action��ӳ���ϵ��
       |
       |__����Java��İ�����������Ҫ�ĵ�Ȼ�ǰ���action�İ�



2.Eclipse��StrutsӦ�ø���β��ã�

  a.Eclipse��Ĭ�Ͻ�srcĿ¼�µ�����Դ�������������ڷ������ߵ���/����ʱ�ŵ�WEB-INF/classesĿ¼�£����ԣ�
    i.��д�ĸ���Java��Դ��ֱ�ӷ���srcĿ¼�£�Ҫ����·����
    ii.��Ȼstruts.xml������룬��Eclipse������ѭ������򣬾���srcĿ¼�µ��������ݷ���ʱĬ�ϼ��ص�WEB-INF/classes��
    iii.����struts.xml����ֱ�Ӵ�����srcĿ¼��
����С�᣺��ס��Eclipse�£�srcĿ¼�µ����ݣ�Ŀ¼�ṹ�����м���ʱֱ�Ӷ�ӦWEB-INF/classes
����Eclipse�µ�WebContentĿ¼��Ӧ����ʱ��Ӧ�ó����Ŀ¼

  b.��JRE/Web����ӦWEB-INF/libĿ¼��
    i.����WebӦ�ÿ϶�����Ҫ����Web���������У�������ߵĹ�ϵ��Web��������WebӦ�ã����WebӦ�õ�lib������������ĺ������
    ii.����WEB-INF/lib���ô��Tomcat�ĺ������
    iii.���Tomcat��WebӦ�ö�����JRE���У�������ߵĹ�ϵ��JRE����Tomcat��Tomcat����WebӦ�ã����WebӦ�õ�libҲ�������JRE���
    iv.JRE����Ƿ�����������ϵͳ��CLASSPATH�е�
    v.���WEB-INFֻ�ܴ�ų�����������֮���Լ����õ�����⣺�ر���WebӦ���Լ��õ�Web�����⣨��������⣬��Struts��Hibernate��Spring�ȣ����Լ�һЩ�Լ����������
    vi.��������JRE��Tomcat���ڡ��󻷾���������Ӧ�ö����������������ú����������WEB-INF/�ļ��أ���Strutsֻ��Ӧ�ó��򼶱�ģ����ڡ�С�����������������ú���Ҫ��Deployment�����WEB-INF/���ز���
��������һ��"struts��װ�ʹ.txt"������
����С�᣺��ס��Eclipse�У�ֻ��Libraries�µ�user libraries�ڼ����ǲŶ�ӦWEB-INF/lib

  c.����srcֱ�Ӷ�ӦWEB-INF/classes����web.xmlӦ��ֱ�ӷ���Ӧ�ó����Ŀ¼�£���˿���ʱ���ܽ�web.xml������src/��Ӧ��ֱ�Ӵ�����WebContent/�£���Ȼ�ڴ������̵�ʱ����Թ�ѡ�Զ�����web.xmlѡ��ֱ���Զ�������WebContent�д�����
������һ��"struts��װ�ʹ.txt"�н�����


3.����Struts����

  a.������һ�£���һ����Struts�����Ѿ�����

  b.��src�д���struts.xml����Struts�������ļ������涨������action��ӳ�䣬һ��Լ���׳Ƶ�ӳ�������xxx.actionӳ�䵽XxxAction�࣬ӳ�䶯����ִ����Struts����Filter��ְ��

  c.���Ŵ���XxxAction�࣬�̳�ActionSupport�ࣨStruts API������һ���������������������������������ĸ�model������������󽫽�����ظ�Struts����Filter

  d.������Filter���ݷ��ؽ�����������ĸ�JSPҳ����û���ʾ���ĸ�����ֵӳ�䵽�ĸ�JSPҳ��Ҳ����struts.xml��ָ����
����С�᣺struts.xml��ʵ����һ������action����ļ�����������������action��ӳ���ϵ������Ҳ�����˸�action�ķ���ֵ��JSPҳ���ӳ���ϵ

  e.С�᣺��Struts����֮��Ĺ�������
    i.���action�Լ�ҵ����model
    ii.��struts.xml��������Ӧ��actionӳ�䣨��������ֵӳ��JSP��
    iii.���JSPҳ�棬ѡ���ĸ�JSPҳ����ʾ���û�����action�ķ���ֵ�����ģ�JSP�п���ʹ��Struts�ṩ�ı�ǩ��


4.StrutsTestʾ���������Ŀ¼�и���