package fop.model.player;

import java.io.PrintWriter;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ScoreEntry implements Comparable<ScoreEntry> {

	private String name;
	private Date date;
	private int score;

	/**
	 * creates a score entry
	 * 
	 * @param name
	 * @param score
	 * @param date
	 */
	public ScoreEntry(String name, int score, Date date) {
		this.name = name;
		this.score = score;
		this.date = date;
	}

	/**
	 * creates a score entry via player
	 * 
	 * @param player
	 */
	public ScoreEntry(Player player) {
		this.name = player.getName();
		this.score = player.getScore();
		this.date = new Date();
	}

	/**
	 * compares the score
	 */
	@Override
	public int compareTo(ScoreEntry scoreEntry) {
		return Integer.compare(this.score, scoreEntry.score);
	}

	/**
	 * prints a new highscore entry
	 * 
	 * @param printWriter
	 */
	public void write(PrintWriter printWriter) {
		// TODO
		printWriter.write(name+";"+date.getTime()+";"+score+System.lineSeparator());
	}

	/**
	 * reads a score entry and checks if it is allowed
	 * 
	 * @param line
	 * @return
	 */
	public static ScoreEntry read(String line) {
		// TODO
		if (line == null) {
			return null;
        }

        if("".equals(line))
        {
        	return null;
        }

        if(line.split(";").length != 3)
        {
        	return null;
        }

        //if char is enough
        for(int i=0; i <= 2;i++)
        {
            if("".equals(line.split(";")[i]))
            {
            	return null;
            }
        }
        //split the line-String
        String[] info =line.split(";");
        //transform the date 
        String data = info[1];
        Long result =  Long.parseLong(data);	 	
        Date dataforuse=new Date(result);
        
        return new ScoreEntry(info[0], Integer.parseInt(info[2]),dataforuse);
	}

	/**
	 * returns the current Date
	 * 
	 * @return
	 */
	public Date getDate() {
		return date;
	}

	/**
	 * returns the name
	 * 
	 * @return
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * returns the score
	 * 
	 * @return
	 */
	public int getScore() {
		return this.score;
	}

}
