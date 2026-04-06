import java.util.PriorityQueue;

public class ReorganizeString {

    /*
     * s = aaabbc -> ababac
     *
     * pq(int[]) -> (1,0),(1,2)
     *
     * (1,0), (1,2)
     *
     *
     * ans = ababac
     *
     * if(--f1>0){
     * (0,0)
     * }
     * if(--f2>0){
     * (0,2)
     * }
     *
     *
     */

    public String reorganizeString(String s){

        int[] freq = new int[26];

        for(char c : s.toCharArray()){
            freq[c-'a']++;
        }

        PriorityQueue<int[]> pq = new PriorityQueue<>((a,b) -> b[0]-a[0]);

        for(int i=0;i<26;i++){
            if(freq[i]>0){
                pq.offer(new int[]{freq[i],i});
            }
        }

        StringBuilder ans = new StringBuilder();

        while(pq.size()>1){

            int[] first = pq.poll();
            int[] second = pq.poll();

            int f1 = first[0];
            int c1 = first[1];

            int f2 = second[0];
            int c2 = second[1];

            ans.append((char)(c1+'a'));
            ans.append((char)(c2+'a'));

            if(--f1>0){
                pq.offer(new int[]{f1,c1});
            }
            if(--f2>0){
                pq.offer(new int[]{f2,c2});
            }
        }

        if(!pq.isEmpty()){
            int[] last = pq.poll();
            int fl = last[0];
            int cl = last[1];

            if(fl>1){
                return "";
            }

            ans.append((char)(cl+'a'));
        }

        return ans.toString();
    }
}
