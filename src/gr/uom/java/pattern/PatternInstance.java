package gr.uom.java.pattern;

import java.util.*;

public class PatternInstance {
    private Set<PatternInstance.Entry> entrySet;
    private int instanceCounter;

    public PatternInstance() {
        entrySet = new LinkedHashSet<PatternInstance.Entry>();
    }

    public void addEntry(Entry e) {
        entrySet.add(e);
    }

    public Set<PatternInstance.Entry> getEntrySet() {
		return entrySet;
	}

	public Iterator<PatternInstance.Entry> getRoleIterator() {
        return entrySet.iterator();
    }
    
	public int getInstanceCounter() {
		return instanceCounter;
	}

	public void setInstanceCounter(int instanceCounter) {
		this.instanceCounter = instanceCounter;
	}

    public boolean equals(Object o) {
        if(this == o) {
            return true;
        }

        if (o instanceof PatternInstance) {
            PatternInstance instance = (PatternInstance)o;
            
            for(Entry e : instance.entrySet) {
                if(!entrySet.contains(e))
                    return false;
            }
            return true;
            //return entryList.equals(instance.entryList);
        }
        return false;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        for(Entry e : entrySet) {
            sb.append(e.toString()).append(" | ");
        }
        return sb.toString();
    }

    public class Entry {
    	private volatile int hashCode = 0;
        private String roleName;
        private String elementName;
        private int position;
        private RoleType roleType;

        public Entry(RoleType roleType, String roleName, String elementName, int position) {
        	this.roleType = roleType;
            this.roleName = roleName;
            this.elementName = elementName;
            this.position = position;
        }

        public String getRoleName() {
            return roleName;
        }

        public String getElementName() {
            return elementName;
        }

        public int getPosition() {
            return position;
        }

        public RoleType getRoleType() {
			return roleType;
		}

		public boolean equals(Object o) {
            if(this == o) {
                return true;
            }

            if (o instanceof Entry) {
                Entry entry = (Entry)o;

                return roleName.equals(entry.roleName) &&
                    elementName.equals(entry.elementName);
            }
            return false;
        }

        public int hashCode() {
            if (hashCode == 0) {
                int result = 17;
                result = 37*result + roleName.hashCode();
                result = 37*result + elementName.hashCode();
                hashCode = result;
            }
            return hashCode;
        }
        
        public String toString() {
            return roleName + ": " + elementName;
        }
    }
    
    public enum RoleType {
    	CLASS, METHOD, FIELD;
    }
}
