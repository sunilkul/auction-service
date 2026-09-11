import sys, subprocess
import pandas as pd
from datetime import datetime, timedelta
import random, statistics, copy, base64, os

# Input schedule
rows = [
    ("DAY 1","15-Nov-25","MATCH 1","EPAM SHINERS","EPAM STALWARTS","G1","9:00 AM","11:30 AM"),
    ("DAY 1","15-Nov-25","MATCH 2","EPAM SPARTANS","EPAM PHOENIXES","G2","11:30 AM","14:00 PM"),
    ("DAY 1","15-Nov-25","MATCH 3","EPAM RADIANTS","EPAM DYNAMOS","G1","14:00 PM","16:30 PM"),

    ("DAY 2","16-Nov-25","MATCH 4","EPAM GLORIFIERS","EPAM VICTORS","G2","9:00 AM","11:30 AM"),
    ("DAY 2","16-Nov-25","MATCH 5","EPAM SPARKS","EPAM SHINERS","G1","11:30 AM","14:00 PM"),
    ("DAY 2","16-Nov-25","MATCH 6","EPAM PHOENIXES","EPAM GUARDIANS","G2","14:00 PM","16:30 PM"),

    ("DAY 3","22-Nov-25","MATCH 7","EPAM DYNAMOS","EPAM SPARKS","G1","9:00 AM","11:30 AM"),
    ("DAY 3","22-Nov-25","MATCH 8","EPAM VICTORS","EPAM PHOENIXES","G2","11:30 AM","14:00 PM"),
    ("DAY 3","22-Nov-25","MATCH 9","EPAM SHINERS","EPAM RADIANTS","G1","14:00 PM","16:30 PM"),

    ("DAY 4","23-Nov-25","MATCH 10","EPAM SPARTANS","EPAM GLORIFIERS","G2","9:00 AM","11:30 AM"),
    ("DAY 4","23-Nov-25","MATCH 11","EPAM STALWARTS","EPAM DYNAMOS","G1","11:30 AM","14:00 PM"),
    ("DAY 4","23-Nov-25","MATCH 12","EPAM GUARDIANS","EPAM VICTORS","G2","14:00 PM","16:30 PM"),

    ("DAY 5","29-Nov-25","MATCH 13","EPAM STALWARTS","EPAM RADIANTS","G1","9:00 AM","11:30 AM"),
    ("DAY 5","29-Nov-25","MATCH 14","EPAM SPARTANS","EPAM GUARDIANS","G2","11:30 AM","14:00 PM"),
    ("DAY 5","29-Nov-25","MATCH 15","EPAM DYNAMOS","EPAM SHINERS","G1","14:00 PM","16:30 PM"),

    ("DAY 6","30-Nov-25","MATCH 16","EPAM RADIANTS","EPAM SPARKS","G1","9:00 AM","11:30 AM"),
    ("DAY 6","30-Nov-25","MATCH 17","EPAM SPARTANS","EPAM VICTORS","G2","11:30 AM","14:00 PM"),
    ("DAY 6","30-Nov-25","MATCH 18","EPAM GLORIFIERS","EPAM GUARDIANS","G2","14:00 PM","16:30 PM"),

    ("DAY 7","6-Dec-25","MATCH 19","EPAM PHOENIXES","EPAM GLORIFIERS","G2","9:00 AM","11:30 AM"),
    ("DAY 7","6-Dec-25","MATCH 20","EPAM SPARKS","EPAM STALWARTS","G1","11:30 AM","14:00 PM"),
]

# normalize and create dataframe
def parse_date(ds):
    return datetime.strptime(ds, "%d-%b-%y").date()

def parse_start_time(t):
    t = t.strip()
    if '14:00' in t:
        return 14*60
    if '9:00' in t:
        return 9*60
    if '11:30' in t:
        return 11*60 + 30
    try:
        dt = datetime.strptime(t, '%I:%M %p')
        return dt.hour*60 + dt.minute
    except:
        return None

cols = ['DayLabel','DateStr','MatchNo','TeamA','TeamB','Group','Start','End']
df = pd.DataFrame(rows, columns=cols)
df['Date'] = df['DateStr'].apply(parse_date)
df['StartMins'] = df['Start'].apply(parse_start_time)

def timeslot_label(mins):
    if mins == 9*60:
        return 'Morning'
    if mins == 11*60+30:
        return 'Afternoon'
    if mins >= 14*60:
        return 'Evening'
    return 'Other'

df['Slot'] = df['StartMins'].apply(timeslot_label)

# 1) team-wise counts
teams = sorted(set(df['TeamA']).union(df['TeamB']))
team_counts = {t: {'Morning':0,'Afternoon':0,'Evening':0} for t in teams}
for _,r in df.iterrows():
    for t in (r['TeamA'], r['TeamB']):
        team_counts[t][r['Slot']] += 1
team_counts_df = pd.DataFrame.from_dict(team_counts, orient='index').reset_index().rename(columns={'index':'Team'})
team_counts_df['Total'] = team_counts_df[['Morning','Afternoon','Evening']].sum(axis=1)

# 2) weekend continuity (Saturday-Sunday pairs)
dates = sorted(df['Date'].unique())
weekend_pairs = []
for d in dates:
    if d.weekday() == 5: # Saturday
        sd = d + timedelta(days=1)
        if sd in dates:
            teams_sat = set(df[df['Date']==d]['TeamA']).union(set(df[df['Date']==d]['TeamB']))
            teams_sun = set(df[df['Date']==sd]['TeamA']).union(set(df[df['Date']==sd]['TeamB']))
            both = sorted(list(teams_sat.intersection(teams_sun)))
            weekend_pairs.append({'Weekend': f"{d.strftime('%d-%b-%y')} - {sd.strftime('%d-%b-%y')}",'Saturday':d.strftime('%d-%b-%y'),'Sunday':sd.strftime('%d-%b-%y'),'TeamsBoth':'; '.join(both)})
weekend_df = pd.DataFrame(weekend_pairs)

# 3) per-team gaps
team_gaps = []
for t in teams:
    dates_t = sorted(list(df[df['TeamA']==t]['Date']) + list(df[df['TeamB']==t]['Date']))
    dates_t = sorted(dates_t)
    gaps = [ (dates_t[i+1]-dates_t[i]).days for i in range(len(dates_t)-1) ]
    avg = round(sum(gaps)/len(gaps),3) if gaps else None
    team_gaps.append({'Team':t,'MatchDates':', '.join(d.strftime('%d-%b-%y') for d in dates_t),'Gaps':', '.join(str(g) for g in gaps),'AvgGap':avg})
team_gaps_df = pd.DataFrame(team_gaps).sort_values('AvgGap')

# 4) optimizer: allow cross-day swaps to rebalance gaps
slots = [ (i, row['Date']) for i,row in df.reset_index().iterrows() ]
matches = []
for i,row in df.reset_index().iterrows():
    matches.append({'idx':i,'MatchNo':row['MatchNo'],'TeamA':row['TeamA'],'TeamB':row['TeamB'],'Group':row['Group']})

def objective(assignment):
    team_dates = {t:[] for t in teams}
    for s_idx,match in assignment.items():
        date = slots[s_idx][1]
        team_dates[match['TeamA']].append(date)
        team_dates[match['TeamB']].append(date)
    obj = 0.0
    for t,ds in team_dates.items():
        ds_sorted = sorted(ds)
        if len(ds_sorted) < 2:
            continue
        gaps = [(ds_sorted[i+1]-ds_sorted[i]).days for i in range(len(ds_sorted)-1)]
        if len(gaps) > 0:
            var = statistics.pvariance(gaps)
            obj += var + (sum(gaps)/len(gaps))
    return obj

assignment = {i:matches[i] for i in range(len(matches))}
best_assign = copy.deepcopy(assignment)
best_obj = objective(best_assign)
random.seed(0)
ITER = 8000
for it in range(ITER):
    i,j = random.sample(range(len(matches)),2)
    new_assign = copy.deepcopy(best_assign)
    new_assign[i], new_assign[j] = new_assign[j], new_assign[i]
    def check_local(a, idx):
        d = slots[idx][1]
        teams_here = []
        for s_idx,match in a.items():
            if slots[s_idx][1] == d:
                teams_here.extend([match['TeamA'], match['TeamB']])
        return len(teams_here) == len(set(teams_here))
    if not (check_local(new_assign,i) and check_local(new_assign,j)):
        continue
    obj = objective(new_assign)
    if obj < best_obj:
        best_assign = new_assign
        best_obj = obj

opt_rows = []
for s_idx,slot in enumerate(slots):
    match = best_assign[s_idx]
    orig = df.iloc[s_idx]
    opt_rows.append({'DayLabel':orig['DayLabel'],'Date':slot[1].strftime('%d-%b-%y'),'MatchNo':match['MatchNo'],'TeamA':match['TeamA'],'TeamB':match['TeamB'],'Group':match['Group'],'Start':orig['Start'],'End':orig['End']})
opt_df = pd.DataFrame(opt_rows)

outdir = os.path.join(os.getcwd(), 'result')
if not os.path.exists(outdir):
    os.makedirs(outdir)
out1 = os.path.join(outdir, 'Team_Time_Counts.csv')
out2 = os.path.join(outdir, 'Weekend_Continuity.csv')
out3 = os.path.join(outdir, 'Team_Gaps.csv')
out4 = os.path.join(outdir, 'Original_Schedule.csv')
out5 = os.path.join(outdir, 'Optimized_Schedule.csv')

team_counts_df.to_csv(out1, index=False)
(weekend_df if not weekend_df.empty else pd.DataFrame([{'Info':'No Saturday-Sunday pairs found in dates'}])).to_csv(out2, index=False)
team_gaps_df.to_csv(out3, index=False)
df[['DayLabel','DateStr','MatchNo','TeamA','TeamB','Group','Start','End']].to_csv(out4, index=False)
opt_df.to_csv(out5, index=False)

print('FILES_CREATED')
print(out1)
print(out2)
print(out3)
print(out4)
print(out5)
