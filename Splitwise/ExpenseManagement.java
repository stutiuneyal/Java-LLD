import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExpenseManagement {

    // balanceMap: creditor -> (debtor -> balance): debtor owes creditor
    private Map<User, Map<User,Balance>> balanceMap = new HashMap<>();

    public void add(Double amount, User paidBy, List<User> paidFor) {

        if (amount == null) {
            return;
        }

        if (!paidFor.contains(paidBy)) {
            paidFor.add(0, paidBy);
        }

        // Expense division calculate -> 2 decimal places
        Double expDiv = amount / (double)paidFor.size();
        double splitAmount = Double.parseDouble(String.format("%.2f", expDiv));

        // Update the Balance
        Map<User,Balance> userBalanceMap = new HashMap<>();

        for(User user : paidFor){
            if(!user.getId().equals(paidBy.getId())){
                Balance balance = new Balance(paidBy, amount, user, splitAmount);
                userBalanceMap.put(user, balance);
            }
        }

        if(balanceMap.containsKey(paidBy)){
            Map<User,Balance> alreadyPresent = balanceMap.get(paidBy);

            for(User paidForUser : userBalanceMap.keySet()){
                if(alreadyPresent.containsKey(paidForUser)){
                    Double updatedAmount = userBalanceMap.get(paidForUser).getUserBalance() + alreadyPresent.get(paidForUser).getUserBalance();
                    Balance updatedBalance = alreadyPresent.get(paidForUser);
                    updatedBalance.setUserBalance(updatedAmount);
                    alreadyPresent.put(paidForUser, updatedBalance);
                }else{
                    alreadyPresent.put(paidForUser, userBalanceMap.get(paidForUser));
                }
            }
        }else{
            balanceMap.put(paidBy, userBalanceMap);
        }

        printOweDetails();


    }

    public void settle(User paying, User payTo) {

        if(!balanceMap.containsKey(payTo)){
            System.out.println(payTo.getName()+" does not owes any amount to "+paying.getName());
            return;
        }

        Map<User,Balance> userBalance = balanceMap.get(payTo);

        if(!userBalance.containsKey(paying)){
            System.out.println(payTo.getName()+" does not owes any amount to "+paying.getName());
            return;
        }

        userBalance.remove(paying);

        balanceMap.put(payTo, userBalance);

        printOweDetails();

    }

    public void simplifyDebt(){

        // Build the net balance map
        Map<User,Double> net = new HashMap<>();

        for(Map.Entry<User,Map<User,Balance>> creditorEntry : balanceMap.entrySet()){
            User creditor = creditorEntry.getKey();
            Map<User,Balance> debtorsMap = creditorEntry.getValue();

            for(Map.Entry<User,Balance> debtorEntry : debtorsMap.entrySet()){
                User debtor = debtorEntry.getKey();
                double amt = debtorEntry.getValue().getUserBalance();

                net.put(creditor, net.getOrDefault(creditor, 0.0)+amt);
                net.put(debtor, net.getOrDefault(debtor, 0.0)-amt);
            }
        }

        // Build two lists -> (debtors & creditors)
        List<User> creditors = new ArrayList<>();
        List<User> debtors = new ArrayList<>();

        for(Map.Entry<User,Double> e : net.entrySet()){
            double val = Double.parseDouble(String.format("%.2f", e.getValue()));
            net.put(e.getKey(), val);

            if(val>0){
                creditors.add(e.getKey());
            }else if(val<0){
                debtors.add(e.getKey());
            }
        }

        // Final simplification

        // Replace the old map
        balanceMap.clear();

        int i=0,j=0;

        while(i<debtors.size() && j<creditors.size()){
            User debtor = debtors.get(i);
            User creditor = creditors.get(j);

            double debtorOwes = -net.get(debtor); // positive
            double creditorOwes = net.get(creditor);

            double pay = Double.parseDouble(String.format("%.2f",Math.min(debtorOwes,creditorOwes)));

            // Record it in the balanceMap
            balanceMap.computeIfAbsent(creditor, k -> new HashMap<>())
            .put(debtor, new Balance(creditor, pay, debtor, pay));

            // update the net balances
            net.put(creditor, Double.parseDouble(String.format("%.2f", net.get(creditor)-pay)));
            net.put(debtor, Double.parseDouble(String.format("%.2f", net.get(debtor)+pay)));

            if(net.get(debtor)==0){
                i++;
            }
            if(net.get(creditor)==0){
                j++;
            }
        }

        printOweDetails();

    }

    public void printOweDetails(){

        if(balanceMap.size()==0){
            System.out.println("No Expenses!!!");
            return;
        }

        for(User paidBy : balanceMap.keySet()){
            Map<User,Balance> paidForMap = balanceMap.get(paidBy);

            for(User paidFor : paidForMap.keySet()){
                System.out.println(paidFor.getName() +" owes "+ paidBy.getName()+ " amount: "+paidForMap.get(paidFor).getUserBalance());
            }
        }
    }

}
