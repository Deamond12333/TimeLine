package comdeamond18.vk.timeline;

import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;

import java.util.ArrayList;
import java.util.StringTokenizer;

public class Parser
{
    public Parser()
    {
    }

    public String getDayOfWeek(String day)
    {
        char[] input = day.toCharArray();
        for (int i = 0; i < input.length; ++i)
        {
            if (input[i] == ',')
            {
                return day.substring(0, i);
            }
        }
        return null;
    }

    public String getDate(String day)
    {
        char[] input = day.toCharArray();
        for (int i = input.length-1; i > -1; --i)
        {
            if (input[i] == ' ')
            {
                return day.substring(i+1, day.length());
            }
        }
        return null;
    }

    public String getTineBegin(String time)
    {
        char[] input = time.toCharArray();
        for (int i = 0; i < input.length; ++i)
        {
            if ((input[i] == '0')||(input[i] == '1')||(input[i] == '2')||(input[i] == '3')||(input[i] == '4')||(input[i] == '5')||(input[i] == '6')||(input[i] == '7')||(input[i] == '8')||(input[i] == '9'))
            {
                return time.substring(i, i+5);
            }
        }
        return null;
    }

    public String getTineEnd(String time)
    {
        char[] input = time.toCharArray();
        for (int i = input.length-1; i > -1; --i)
        {
            if ((input[i] == '0')||(input[i] == '1')||(input[i] == '2')||(input[i] == '3')||(input[i] == '4')||(input[i] == '5')||(input[i] == '6')||(input[i] == '7')||(input[i] == '8')||(input[i] == '9'))
            {
                return time.substring(i-4, i+1);
            }
        }
        return null;
    }

    public void getSubjectsParameters(String text, Subject subject)
    {
        StringTokenizer tokenizer = new StringTokenizer(text, "\r\n");
        ArrayList<String> background = new ArrayList<>();

        while (tokenizer.hasMoreTokens())
        {
            String pufel = tokenizer.nextToken();
            background.add(pufel.trim());
        }

        subject.subject = background.get(0)+"\n"+background.get(1)+"\n"+background.get(2) + " " + background.get(3)+"\n"+background.get(4);
    }

    public ArrayList<AcademDay> parseHTML(String html)
    {
        HtmlCleaner cleaner = new HtmlCleaner();
        TagNode rootNode = cleaner.clean(html);
        TagNode getTables[] = rootNode.getElementsByName("table", true);

        ArrayList<AcademDay> schedule = new ArrayList<>();

        for (TagNode table : getTables)
        {
            AcademDay today = new AcademDay();
            TagNode tags[] = table.getChildTags();//день недели и пары

            TagNode thead[] = tags[0].getChildTags();//день недели и число
            TagNode fignuy[] = thead[0].getChildTags();
            String day = fignuy[0].getText().toString();

            today.date = getDate(day);
            today.dayOfWeek = getDayOfWeek(day);

            ArrayList<Subject> subjects = new ArrayList<>();

            for (int i = 1; i < tags.length; ++i)
            {
                TagNode tagi[] = tags[i].getChildTags(); //получили 3 тега, берем 1
                TagNode vremy[] = tagi[0].getChildTags(); //получили 2 тегов, в 0 - время, в 1 - 0: предмет, 2: препод, 4: тип предмета и аудитория, 6: поток

                Subject subject = new Subject();

                String time = vremy[0].getText().toString();
                subject.time = getTineBegin(time)+"\n\n"+getTineEnd(time);

                String para = vremy[1].getText().toString();

                getSubjectsParameters(para, subject);

                subjects.add(subject);
            }

            today.subjects = subjects;
            schedule.add(today);
        }
        return schedule;
    }

    public String toASCII (String input)
    {
        String result = new String();
        for (int i = 0; i < input.length(); ++i)
        {
            switch (input.substring(i, i+1))
            {
                case "А": result += "%C0"; break;
                case "Б": result += "%C1"; break;
                case "В": result += "%C2"; break;
                case "Г": result += "%C3"; break;
                case "Д": result += "%C4"; break;
                case "Е": result += "%C5"; break;
                case "Ё": result += "%A8"; break;
                case "Ж": result += "%C6"; break;
                case "З": result += "%C7"; break;
                case "И": result += "%C8"; break;
                case "Й": result += "%C9"; break;
                case "К": result += "%CA"; break;
                case "Л": result += "%CB"; break;
                case "М": result += "%CC"; break;
                case "Н": result += "%CD"; break;
                case "О": result += "%CE"; break;
                case "П": result += "%CF"; break;
                case "Р": result += "%D0"; break;
                case "С": result += "%D1"; break;
                case "Т": result += "%D2"; break;
                case "У": result += "%D3"; break;
                case "Ф": result += "%D4"; break;
                case "Х": result += "%D5"; break;
                case "Ц": result += "%D6"; break;
                case "Ч": result += "%D7"; break;
                case "Ш": result += "%D8"; break;
                case "Щ": result += "%D9"; break;
                case "Ъ": result += "%DA"; break;
                case "Ы": result += "%DB"; break;
                case "Ь": result += "%DС"; break;
                case "Э": result += "%DD"; break;
                case "Ю": result += "%DE"; break;
                case "Я": result += "%DF"; break;
                case " ": result += "%20"; break;
                default: result += input.substring(i, i+1);
            }
        }
        return result;
    }
}
