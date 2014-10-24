package droolstest;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
    		KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
    		kbuilder.add(ResourceFactory.newClassPathResource("drools/a.drl"), ResourceType.DRL);
    		if (kbuilder.hasErrors()) {
    			System.out.println("πÊ‘Ú¥ÌŒÛ£∫");
    			Iterator<KnowledgeBuilderError> it = kbuilder.getErrors().iterator();
    			while (it.hasNext()) System.out.println(it.next());
    			return;
    		}
    		KnowledgeBase kb = KnowledgeBaseFactory.newKnowledgeBase();
    		kb.addKnowledgePackages(kbuilder.getKnowledgePackages());
    		StatefulKnowledgeSession s = kb.newStatefulKnowledgeSession();
    		s.insert(new User().setMoney(50));
    		s.fireAllRules();
    		s.dispose();
    }
}
