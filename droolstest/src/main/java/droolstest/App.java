package droolstest;

import java.util.Iterator;

import org.kie.api.io.ResourceType;
import org.kie.internal.KnowledgeBase;
import org.kie.internal.KnowledgeBaseFactory;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderError;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.runtime.StatefulKnowledgeSession;

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
