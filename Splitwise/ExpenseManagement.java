import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExpenseManagement {

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
