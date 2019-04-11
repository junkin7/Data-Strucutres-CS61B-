package bearmaps.hw4;
import bearmaps.proj2ab.DoubleMapPQ;
import edu.princeton.cs.algs4.Stopwatch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class AStarSolver<Vertex> implements ShortestPathsSolver<Vertex> {
    private DoubleMapPQ<Vertex> pq;
    private HashMap<Vertex, Double> disTo;
    private HashMap<Vertex, Vertex> edgeTo;
    private List<Vertex> marked;

    private LinkedList<Vertex> solution;
    private double weight;
    private SolverOutcome outcome;
    private int states;

    private Stopwatch sw;
    private double time;
    private double timeOut;

    public AStarSolver(AStarGraph<Vertex> input, Vertex start, Vertex end, double timeout) {
        sw = new Stopwatch();
        pq = new DoubleMapPQ<>();
        marked = new ArrayList<>();
        disTo = new HashMap<>();
        edgeTo = new HashMap<>();
        solution = new LinkedList<>();
        timeOut = timeout;

        disTo.put(start, 0.0);
        edgeTo.put(start, null);
        pq.add(start, -1000.0);
        queue(input, start, end);
        time = sw.elapsedTime();
    }

    private void queue(AStarGraph<Vertex> input, Vertex start, Vertex end) {
        List<WeightedEdge<Vertex>> neighbors = input.neighbors(start);
        int i = 1;
        for (WeightedEdge<Vertex> edge : neighbors) {
            Vertex v = edge.to();
            double storedDis = disTo.get(edge.from()) + edge.weight();
            double dis = storedDis + input.estimatedDistanceToGoal(v, end);
            if (pq.contains(v) && storedDis < disTo.get(v)) {
                pq.changePriority(v, dis);
                disTo.put(v, storedDis);
                edgeTo.put(v, start);
            } else if (!pq.contains(v) && !marked.contains(v)) {
                pq.add(v, dis);
                disTo.put(v, storedDis);
                edgeTo.put(v, start);
            }
        }
        start = relax(end);
        if (start == null) {
            return;
        }
        queue(input, start, end);
    }

    private Vertex relax(Vertex end) {
        Vertex v = pq.removeSmallest();
        marked.add(v);
        states += 1;
        time = sw.elapsedTime();

        if (time > timeOut) {
            outcome = SolverOutcome.TIMEOUT;
            weight = 0;
            solution.clear();
            return null;
        } else if (v.equals(end)) {
            weight = disTo.get(end);
            while (edgeTo.get(v) != null) {
                solution.addFirst(v);
                v = edgeTo.get(v);
            }
            solution.addFirst(v);
            outcome = SolverOutcome.SOLVED;
            return null;
        } else if (pq.size() == 0) {
            outcome = SolverOutcome.UNSOLVABLE;
            weight = 0;
            solution.clear();
            return null;
        }
        return v;
    }

    public SolverOutcome outcome() {
        return outcome;
    }

    public List<Vertex> solution() {
        return solution;
    }

    public double solutionWeight() {
        return weight;
    }

    public int numStatesExplored() {
        return states;
    }

    public double explorationTime() {
        return time;
    }
}
