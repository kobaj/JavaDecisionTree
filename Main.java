package kobaj;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Main
{
	
	/**
	 * Main jump
	 * 
	 * @param args
	 */
	public static void main(String[] args)
	{
		System.out.println("begin");
		
		// conditions are values for attributes
		Condition gr_20 = new Condition(">20");
		Condition gr_50 = new Condition(">50");
		Condition gr_80 = new Condition(">80");
		Condition yes = new Condition("yes");
		Condition no = new Condition("no");
		Condition scattered = new Condition("scattered");
		Condition overcast = new Condition("overcast");
		
		Attribute[] real_attributes = new Attribute[3];
		real_attributes[0] = new Attribute("%", gr_20, gr_50, gr_80);
		real_attributes[1] = new Attribute("rained", yes, no);
		real_attributes[2] = new Attribute("cloudy", scattered, no, overcast);
		
		Attribute[] example_attributes = new Attribute[6];
		example_attributes[0] = new Attribute("%", gr_20, gr_50, gr_80);
		example_attributes[1] = new Attribute("%", gr_20, gr_50, gr_80);
		example_attributes[2] = new Attribute("%", gr_20, gr_50, gr_80);
		example_attributes[3] = new Attribute("rained", yes, no);
		example_attributes[4] = new Attribute("cloudy", scattered, no, overcast);
		example_attributes[5] = new Attribute("rain_today", yes, no);
		
		// examples contain attributes and a value
		// here we will build a decision tree based on 'weather'
		Example[] examples = new Example[10];
		examples[0] = new Example(example_attributes, gr_20, gr_50, no, no, scattered, yes);
		examples[1] = new Example(example_attributes, gr_20, no, no, no, no, no);
		examples[2] = new Example(example_attributes, no, no, no, yes, no, no);
		examples[3] = new Example(example_attributes, gr_20, gr_50, gr_80, no, overcast, yes);
		examples[4] = new Example(example_attributes, gr_20, gr_50, gr_80, yes, no, yes);
		examples[5] = new Example(example_attributes, gr_20, gr_50, gr_80, yes, overcast, yes);
		examples[6] = new Example(example_attributes, gr_20, no, no, yes, no, no);
		examples[7] = new Example(example_attributes, no, no, no, no, no, no);
		examples[8] = new Example(example_attributes, gr_20, no, no, no, overcast, yes);
		examples[9] = new Example(example_attributes, gr_20, no, no, no, scattered, yes);
		
		Attribute desired_attribute = example_attributes[5];
		
		Node<?> tree = learnDecision(examples, real_attributes, yes, desired_attribute);
		
		System.out.println(tree.toString());
	}
	
	/**
	 * Main learn loop that builds a decision tree
	 * 
	 * @param examples list of input samples built from attributes and conditions
	 * @param attributes list of all attributes to iterate over 
	 * @param default_label list of default condition for desired attribute to apply
	 * @param desired_attribute the attribute to test for
	 * @return
	 */
	public static Node<?> learnDecision(Example[] examples, Attribute[] attributes, Condition default_label, Attribute desired_attribute)
	{
		// first base case
		if (examples.length == 0)
		{
			System.out.println("leaf 1");
			return new Node<Condition>(default_label, "first leaf ");
		}
		
		// second base case
		ArrayList<Example> example_copy = new ArrayList<Example>();
		for(Example e: examples)
			example_copy.add(e);
		Collections.sort(example_copy, new ExampleComparator());
		if(example_copy.get(0).get_label().equals(//
				example_copy.get(example_copy.size() - 1).get_label()))
		{
			System.out.println("leaf 2");
			return new Node<Condition>(examples[0].get_label(), "second leaf ");
		}
		
		// third base case
		if (attributes.length == 0)
		{
			Condition mode = Mode(examples);
			System.out.println("leaf 3");
			return new Node<Condition>(mode, "third leaf ");
		}
		
		// recurse
		Attribute best = ChooseBestAttribute(examples, attributes, desired_attribute);
		System.out.println("Finding a best: " + best.name);
		Node<Attribute> tree = new Node<Attribute>(best, "middle node ");
		Condition label = Mode(examples);
		
		for (Condition c : best.possible_conditions)
		{
			Example[] example_i = best.satisfied(examples, c);
			Node<?> sub_tree = learnDecision(example_i, removeBest(attributes, best), label, desired_attribute);
			sub_tree.identifier += c.toString();
			
			tree.children.add(sub_tree);
		}
		
		return tree;
	}
	
	/**
	 * Takes in an array of attributes and removes one.
	 * 
	 * @param attributes array of attributes
	 * @param best attribute to be removed
	 * @return an array of attributes
	 */
	public static Attribute[] removeBest(Attribute[] attributes, Attribute best)
	{
		ArrayList<Attribute> modified_attributes = new ArrayList<Attribute>();
		
		for (Attribute a : attributes)
		{
			if (!a.equals(best))
				modified_attributes.add(a);
		}
		
		return modified_attributes.toArray(new Attribute[0]);
	}
	
	/**
	 * Calculates the mathematical mode condition (based on desired attribute, aka, last attribute in example).
	 * 
	 * @param examples
	 * @return condition that appears most often
	 */
	public static Condition Mode(Example[] examples)
	{
		Condition max_condition = null;
		int max_count = 0;
		
		// not the most efficient
		for (Example e : examples)
		{
			int local_count = 0;
			for (Example inner_e : examples)
			{
				if (inner_e.get_label().equals(e.get_label()))
					local_count++;
			}
			
			if (local_count > max_count)
			{
				max_count = local_count;
				max_condition = e.get_label();
			}
		}
		
		System.out.println("Mode value: " + max_condition);
		
		return max_condition;
	}
	
	/**
	 * This will calculate the Gain and Remainder of attributes and picks the best to recurse over
	 * 
	 * @param examples
	 * @param attributes
	 * @param desired_attribute
	 * @return
	 */
	public static Attribute ChooseBestAttribute(Example[] examples, Attribute[] attributes, Attribute desired_attribute)
	{
		
		Attribute best = null;
		double smallest_double = Double.MAX_VALUE;
		
		for (Attribute a : attributes)
		{
			double remain = Remain(examples, a, desired_attribute);
			if (best == null || remain < smallest_double)
			{
				smallest_double = remain;
				best = a;
			}
		}
		
		return best;
	}
	
	/**
	 * Computes the Remain,
	 * sum(p/t * I(pi/ti, ni/ti));
	 * 
	 * @param examples
	 * @param attribute
	 * @param desired_attribute
	 * @return a double value for the remain
	 */
	public static double Remain(Example[] examples, Attribute attribute, Attribute desired_attribute)
	{
		System.out.println("\nrunning a rem for: " + attribute.name);
		
		double total_examples = examples.length;
		
		double total = 0;
		
		// figure out each attribute
		for (Condition major_condition : attribute.possible_conditions)
		{
			System.out.println("possible condition: " + major_condition.name);
			
			Example[] sub_examples = attribute.satisfied(examples, major_condition);
			Double total_sub_examples = (double) sub_examples.length;
			double precident = total_sub_examples / total_examples;
			
			System.out.println("Number satisfied: " + total_sub_examples);
			
			// figure out the igain
			ArrayList<Double> sub_example_count = new ArrayList<Double>();
			for (Condition c : desired_attribute.possible_conditions)
			{
				Example[] examples_c = desired_attribute.satisfied(sub_examples, c);
				System.out.println("number of passing sub examples: " + examples_c.length);
				sub_example_count.add(examples_c.length / total_sub_examples);
			}
			double i_gain = IGain(sub_example_count.toArray(new Double[0]));
			System.out.println("iGain: " + i_gain);
			
			double total_local_value = precident * i_gain;
			
			total += total_local_value;
		}
		
		System.out.println("got a result of: " + total);
		
		return total;
	}
	
	/**
	 * computers the I value of Remain
	 * sum(-p(vi)logbase2ofp(vi))
	 * 
	 * @param ds which comes from remain
	 * @return double value of I
	 */
	public static double IGain(Double... ds)
	{
		double final_value = 0;
		for (double d : ds)
		{
			if (d != 0.0)
				final_value += -d * Math.log(d) / Math.log(2.0);
		}
		
		if (Double.isNaN(final_value))
			final_value = 0;
		
		return final_value;
	}
	
}

class ExampleComparator implements Comparator<Example>
{
	@Override
	public int compare(Example o1, Example o2)
	{
		return (o1.get_label()).compareTo(o2.get_label());
	}
}

class Example
{
	public Attribute[] my_attributes;
	public Condition[] my_conditions;
	
	public Condition get_label()
	{
		return my_conditions[my_conditions.length - 1];
	}
	
	public Example(Attribute[] const_attributes, Condition... conditions)
	{
		my_attributes = const_attributes;
		my_conditions = conditions;
	}
	
	public boolean check_condition(Attribute attribute, Condition c)
	{
		for (int i = 0; i < my_attributes.length; i++)
		{
			if (my_attributes[i].equals(attribute))
				if (my_conditions[i].equals(c))
				{
					return true;
				}
		}
		
		return false;
	}
}

class Attribute
{
	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Attribute other = (Attribute) obj;
		if (name == null)
		{
			if (other.name != null)
				return false;
		}
		else if (!name.equals(other.name))
			return false;
		return true;
	}
	
	public String name;
	
	public Condition[] possible_conditions;
	
	public Attribute(String name, Condition... possible_conditions)
	{
		this.name = name;
		this.possible_conditions = possible_conditions;
	}
	
	public Example[] satisfied(Example[] examples, Condition c)
	{
		ArrayList<Example> satisfied_examples = new ArrayList<Example>();
		
		for (Example e : examples)
		{
			if (e.check_condition(this, c))
				satisfied_examples.add(e);
		}
		
		return satisfied_examples.toArray(new Example[0]);
	}
	
	@Override
	public String toString()
	{
		return name;
	}
}

class Condition
{
	public String name;
	
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}
	
	public int compareTo(Condition get_label)
	{
		return (get_label.name.compareTo(name));
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Condition other = (Condition) obj;
		if (name == null)
		{
			if (other.name != null)
				return false;
		}
		else if (!name.equals(other.name))
			return false;
		return true;
	}
	
	public Condition(String name)
	{
		this.name = name;
	}
	
	@Override
	public String toString()
	{
		return name;
	}
}

class Node<T>
{
	public String identifier;
	
	public T data;
	public Node<?> parent = null;
	public List<Node<?>> children;
	
	public Node(T data, String ident)
	{
		this.identifier = ident;
		this.data = data;
		children = new ArrayList<Node<?>>();
	}
	
	public Node(T data)
	{
		this(data, "unset");
	}
	
	public String toString(String tabs)
	{
		String childs = "";
		for (Node<?> n : children)
			childs += n.toString(tabs + "-");
		
		if (childs.equals(""))
			childs = "no children.";
		
		return "\n" + tabs + "Node: " + identifier + " value: " + data.toString() + " children: " + childs;
	}
	
	@Override
	public String toString()
	{
		return toString("");
	}
}
